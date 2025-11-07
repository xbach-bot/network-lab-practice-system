"use client";
import React, { Fragment, useEffect, useRef, useState } from "react";
import styles from "../../../styles/Chat.module.scss";
import classnames from "classnames/bind";
import { useAppSelector } from "@/lib/redux/hooks";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCircle,
  faComment,
  faPaperPlane,
  faXmark,
} from "@fortawesome/free-solid-svg-icons";
import { createChat, fetchChats } from "@/config/api";
import { IChat } from "@/types/backend";
import MessageCard from "./Message.card";
import InputEmoji from "react-input-emoji";
import { LoadingOutlined } from "@ant-design/icons";
import { Flex, Spin } from "antd";

import { message } from "antd";
import socket from "@/utils/socket";

const cx = classnames.bind(styles);

const Chat = () => {
  const [messages, setMessages] = useState<IChat[]>([]);
  const [input, setInput] = useState("");
  const [open, setOpen] = useState<boolean>(false);
  const [notification, setNotification] = useState<boolean>(false);
  const [isTyping, setIsTyping] = useState<boolean>(false);
  const [userTyping, setUserTyping] = useState<string | null>(null);

  const [current, setCurrent] = useState<number>(0);
  const [isFetching, setIsFetching] = useState<boolean>(false);
  const [isInitData, setIsInitData] = useState<boolean>(false);
  const [isSend, setIsSend] = useState<boolean>(false);
  const [prevScrollHeight, setPrevScrollHeight] = useState<number>(0);
  const isAuth = useAppSelector((state) => state.auth.isAuthenticated);

  const user = useAppSelector((state) => state.auth.user);
  const wrapperRef = useRef<HTMLDivElement | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (socket) {
      socket.on("message", (message: any) => {
        if (!open) {
          setNotification(true);
        }

        setMessages((prevMessages: any) => [...prevMessages, message]);

        setIsSend(!isSend);
      });

      return () => {
        socket.off("message");
      };
    }
  }, [isSend]);

  useEffect(() => {
    containerRef.current?.scrollTo(0, containerRef.current.scrollHeight);
  }, [isSend]);

  useEffect(() => {
    if (open && wrapperRef.current) {
      wrapperRef.current.style.transform = "translateX(0)";
      containerRef.current?.scrollTo(0, containerRef.current.scrollHeight);
    }
  }, [open]);

  useEffect(() => {
    const fetchData = async () => {
      setIsInitData(true);
      let result = await fetchChats({ lastPage: true });
      let data = result.data?.result as IChat[];
      let currentPage = result.data?.meta.current as number;

      while (data?.length < 15 && currentPage > 1) {
        currentPage -= 1;
        const prevResult = await fetchChats({ page: currentPage });
        const prevData = prevResult.data?.result as IChat[];
        data = [...prevData, ...data];
      }

      setCurrent(currentPage);
      setIsInitData(false);

      setMessages(data);
    };

    fetchData();
  }, []);

  useEffect(() => {
    if (current > 1) {
      const handleScroll = async () => {
        if (containerRef.current && containerRef.current.scrollTop === 0) {
          setIsFetching(true);
          const result = await fetchChats({ page: current - 1 });
          const data = result.data?.result as IChat[];

          setCurrent(result.data?.meta.current as number);

          setPrevScrollHeight(containerRef.current.scrollHeight);

          setMessages((prevMessages) => [...data, ...prevMessages]);

          setIsFetching(false);
        }
      };

      const container = containerRef.current;
      container?.addEventListener("scroll", handleScroll);

      return () => {
        container?.removeEventListener("scroll", handleScroll);
      };
    }
  }, [current]);

  useEffect(() => {
    if (containerRef.current && !isFetching) {
      containerRef.current.scrollTop =
        containerRef.current.scrollHeight - prevScrollHeight;
    }
  }, [isFetching]);

  const sendMessage = async () => {
    if (!isAuth) {
      message.error("Vui lòng đăng nhập để nhắn tin!");
      return;
    }
    if (!input.trim()) return;

    const data: IChat = {
      content: input,
    };

    await createChat(data);
    socket.emit("message", {
      user: {
        id: user.id,
        name: user.name,
      },
      ...data,
    });
    setInput("");
    socket.emit("stopTyping");
  };

  useEffect(() => {
    if (socket) {
      socket.on("deleteMessage", (message: IChat) => {
        setMessages((prevMessages) =>
          prevMessages.filter((msg) => msg.id !== message.id)
        );
      });
    }
  }, []);

  useEffect(() => {
    if (socket) {
      socket.on("typing", (message: any) => {
        setUserTyping(message);
        setIsTyping(true);
      });
      socket.on("stopTyping", () => setIsTyping(false));
    }
  }, [input]);

  useEffect(() => {
    if (!isAuth) return;

    if (input.length === 0 || input === "</br>") {
      socket.emit("stopTyping");
      return;
    }
    socket.emit("typing", user.name);
    const timeout = setTimeout(() => {
      socket.emit("stopTyping");
    }, 3000);

    return () => {
      clearTimeout(timeout);
    };
  }, [input]);

  const handleKeyDown = (event: KeyboardEvent) => {
    if (event.key === "Enter") {
      if (
        (event as unknown as React.KeyboardEvent<HTMLTextAreaElement>).key ===
          "Enter" &&
        !(event as unknown as React.KeyboardEvent<HTMLTextAreaElement>).shiftKey
      ) {
        event.preventDefault();

        sendMessage();
      }
    }
  };

  const handleOpenChat = () => {
    if (wrapperRef.current) {
      wrapperRef.current.style.opacity = "1";
      wrapperRef.current.style.pointerEvents = "auto";
      wrapperRef.current.style.transform = "translateX(0)";
    }
    setNotification(false);
    setOpen(true);
  };

  const handleCloseChat = () => {
    if (wrapperRef.current) {
      wrapperRef.current.style.opacity = "0";
      wrapperRef.current.style.pointerEvents = "none";
      wrapperRef.current.style.transform = "translateX(100%)";
    }
    setOpen(false);
    setNotification(false);
  };

  return (
    <>
      <div ref={wrapperRef} className={cx("wrapper")}>
        <div className={cx("container")}>
          <div className={cx("messages-wrapper")}>
            <h2 className={cx("message-title")}>
              Tin nhắn
              <FontAwesomeIcon
                onClick={handleCloseChat}
                icon={faXmark}
                className={cx("close")}
              />
            </h2>

            {isFetching && (
              <div className={cx("loading-messages")}>
                <Flex align="center" gap="middle">
                  <Spin indicator={<LoadingOutlined spin />} size="large" />
                </Flex>
              </div>
            )}

            <div ref={containerRef} className={cx("message-container")}>
              {messages?.map((chat: IChat, index) => (
                <MessageCard
                  target={index}
                  messages={messages}
                  setMessages={setMessages}
                  key={chat.id}
                  chat={chat}
                  userId={user.id}
                />
              ))}
              {isTyping && (
                <div className={cx("typing-wrapper")}>
                  <div className={cx("typing-info")}>
                    <p>
                      {" "}
                      <span>{userTyping} </span>đang nhắn:
                    </p>
                  </div>

                  <div className={cx("typing-indicator")}>
                    <span className={cx("dot")}></span>
                    <span className={cx("dot")}></span>
                    <span className={cx("dot")}></span>
                  </div>
                </div>
              )}
            </div>
          </div>

          <div className={cx("input-wrapper")}>
            <div className={cx("input-icon")}>
              <div className={cx("input-content")}>
                <InputEmoji
                  onChange={setInput}
                  value={input}
                  shouldReturn={true}
                  keepOpened={true}
                  language="vi"
                  shouldConvertEmojiToImage={false}
                  onKeyDown={handleKeyDown}
                  placeholder={
                    isAuth
                      ? "Nhập tin nhắn..."
                      : "Vui lòng đăng nhập để nhắn tin"
                  }
                />
              </div>

              <div onClick={sendMessage} className={cx("file-submit")}>
                <FontAwesomeIcon icon={faPaperPlane} />
              </div>
            </div>
          </div>
        </div>
      </div>

      {open || isInitData ? (
        <Fragment />
      ) : (
        <div className={cx("chat-icon")} onClick={handleOpenChat}>
          <div className={cx("icon-wrapper")}>
            <FontAwesomeIcon icon={faComment} className={cx("icon-msg")} />
            {notification ? (
              <FontAwesomeIcon icon={faCircle} className={cx("notification")} />
            ) : (
              <Fragment />
            )}
          </div>
        </div>
      )}
    </>
  );
};

export default Chat;
