"use client";

import { IChat, IUser } from "@/types/backend";
import { Avatar, Button, Flex, Input, Spin } from "antd";
import { useEffect, useRef, useState, useCallback } from "react";

import { CloseOutlined, LoadingOutlined } from "@ant-design/icons";
import {
  createChat,
  createPrivateRoom,
  fetchChatsFromRoom,
  fetchMyRooms,
} from "@/config/api";
import { useAppDispatch, useAppSelector } from "@/lib/redux/hooks";
import socket from "@/utils/socket";
import { setChatVisible } from "@/lib/redux/slice/chat.slice";

interface IProps {
  chatTarget: IUser;
}

const ChatPrivate = (props: IProps) => {
  const { chatTarget } = props;
  const [messageInput, setMessageInput] = useState("");
  const [current, setCurrent] = useState<number>(0);
  const [messages, setMessages] = useState<IChat[]>([]);
  console.log(chatTarget.id);
  
  const [isInitData, setIsInitData] = useState<boolean>(false);

  const [isFetching, setIsFetching] = useState<boolean>(false);
  const [prevScrollHeight, setPrevScrollHeight] = useState<number>(0);

  const [isSend, setIsSend] = useState<boolean>(false);

  const [roomId, setRoomId] = useState<number | null>(null);

  const containerRef = useRef<HTMLDivElement | null>(null);

  const user = useAppSelector((state) => state.auth.user);

  const typingTimeoutRef = useRef<any>(null);
  const [isTypingLocal, setIsTypingLocal] = useState(false);
  const [remoteTypingName, setRemoteTypingName] = useState<string | null>(null);

  const dispatch = useAppDispatch();

  useEffect(() => {
    const onTyping = (payload: any) => {
      try {
        if (!payload) return;
        const rId = payload.roomId ?? payload.room;
        if (!rId || roomId === null) return;
        if (String(rId) !== String(roomId)) return;
        if (payload.userId === user?.id) return;
        setRemoteTypingName(payload.name || payload.username || "Đối phương");
      } catch (e) {
        console.error(e);
      }
    };

    const onStopTyping = (payload: any) => {
      try {
        if (!payload) return;
        const rId = payload.roomId ?? payload.room;
        if (!rId || roomId === null) return;
        if (String(rId) !== String(roomId)) return;
        if (payload.userId === user?.id) return;
        setRemoteTypingName(null);
      } catch (e) {
        console.error(e);
      }
    };

    socket.on("typingPrivate", onTyping);
    socket.on("stopTypingPrivate", onStopTyping);

    const onMessagePrivate = (payload: any) => {
      try {
        if (!payload) return;
        const rId = payload.roomId ?? payload.room;
        if (!rId || roomId === null) return;
        if (String(rId) !== String(roomId)) return;

        if (payload.user && payload.user.id === user?.id) return;
        setIsSend(!isSend);

        setMessages((prev) => [...prev, payload]);


      } catch (e) {
        console.error(e);
      }
    };

    socket.on("messagePrivate", onMessagePrivate);
  }, [roomId, user]);

  const onMessageInputChange = useCallback(
    (value: string) => {
      setMessageInput(value);
      if (!roomId || !user) return;

      try {
        if (!isTypingLocal) {
          socket.emit("typingPrivate", {
            roomId,
            userId: user.id,
            name: user.name,
          });
          setIsTypingLocal(true);
        }

        if (typingTimeoutRef.current) clearTimeout(typingTimeoutRef.current);
        typingTimeoutRef.current = setTimeout(() => {
          try {
            socket.emit("stopTypingPrivate", { roomId, userId: user.id });
          } catch (e) {
            console.error(e);
          }
          setIsTypingLocal(false);
        }, 3000);
      } catch (e) {
        console.error(e);
      }
    },
    [roomId, user, isTypingLocal]
  );

  useEffect(() => {
    if (!roomId) return;

    const fetchData = async () => {
      setIsInitData(true);
      let result = await fetchChatsFromRoom({ lastPage: true, roomId });
      let data = result.data?.result as IChat[];
      let currentPage = result.data?.meta.current as number;

      while (data?.length < 15 && currentPage > 1) {
        currentPage -= 1;
        const prevResult = await fetchChatsFromRoom({
          page: currentPage,
          roomId,
        });
        const prevData = prevResult.data?.result as IChat[];
        data = [...prevData, ...data];
      }

      setCurrent(currentPage);
      setIsInitData(false);

      setMessages(data);
    };

    fetchData();
  }, [roomId]);

  useEffect(() => {
    const fetchRoomId = async () => {
      const roomsRes = await fetchMyRooms();
      let room = null as any;
      if (roomsRes && roomsRes.status === 200) {
        const rooms = roomsRes.data || [];
        room = rooms.find((r: any) => {
          if (!r.participants || r.participants.length !== 2) return false;
          return r.participants.some((p: any) => p.id === chatTarget.id);
        });

        if (room) {
          setRoomId(room.id);
        } else {
          // create new room
          const roomRes = await createPrivateRoom(chatTarget.id);

          if (
            roomRes &&
            (roomRes.status === 200 || roomRes.status === 201) &&
            roomRes.data
          ) {
            setRoomId(roomRes.data.id);
          }
        }
      }
    };
    fetchRoomId();
  }, [chatTarget.id]);

  useEffect(() => {
    containerRef.current?.scrollTo(0, containerRef.current.scrollHeight);
  }, []);

  useEffect(() => {
    if (current > 1 && roomId) {
      const handleScroll = async () => {
        if (containerRef.current && containerRef.current.scrollTop === 0) {
          setIsFetching(true);
          const result = await fetchChatsFromRoom({
            page: current - 1,
            roomId,
          });
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

  const handleSendMessage = async () => {
    if (!messageInput.trim() || !roomId) return;

    try {
      const body = { content: messageInput, roomId: roomId };
      const res = await createChat(body as any);
      if (res && (res.status === 200 || res.status === 201) && res.data) {
        const chat = res.data as IChat;
        setMessages((s) => [...s, chat]);
        setMessageInput("");
        setIsSend(!isSend);
        try {
          if (socket && roomId && user) {
            socket.emit("stopTypingPrivate", { roomId, userId: user.id });

            try {
              if (typingTimeoutRef.current) {
                clearTimeout(typingTimeoutRef.current);
                typingTimeoutRef.current = null;
              }
            } catch (t) {}
            setIsTypingLocal(false);

            try {
              socket.emit("messagePrivate", { ...chat, roomId });
            } catch (e) {
              console.error("socket emit messagePrivate failed", e);
            }
          }
        } catch (e) {
          console.error(e);
        }
      } else {
      }
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    if (containerRef.current && !isFetching) {
      containerRef.current.scrollTop =
        containerRef.current.scrollHeight - prevScrollHeight;
    }
  }, [isFetching, isInitData]);

  useEffect(() => {
    if (containerRef.current && !isFetching && !isInitData) {
      containerRef.current.scrollTo(0, containerRef.current.scrollHeight);
    }
  }, [isSend]);
  return (
    <div
      style={{
        position: "fixed",
        bottom: 0,
        right: 100,
        width: 335,
        height: 455,
        maxWidth: "90vw",
        background: "#fff",
        boxShadow: "0 4px 16px rgba(0,0,0,0.2)",
        borderRadius: 8,
        zIndex: 1200,
        padding: 12,
        display: "flex",
        flexDirection: "column",
      }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          padding: "8px 12px",
          borderBottom: "1px solid #f0f0f0",
          flex: "0 0 48px",
          background: "#fff",
          marginBottom: 0,
        }}
      >
        <Avatar style={{ backgroundColor: "#87d068", marginRight: 8 }}>
          {chatTarget?.name?.charAt(0) ?? "U"}
        </Avatar>
        <div style={{ flex: 1, fontWeight: 600 }}>
          {chatTarget?.name ?? "-"}
        </div>
        <Button
          type="text"
          icon={<CloseOutlined />}
          onClick={() => dispatch(setChatVisible(false))}
        />
      </div>

      <div
        ref={containerRef}
        style={{
          flex: "1 1 auto",
          padding: 12,
          overflowY: "auto",
          background: "#fff",
        }}
      >
        {isInitData ||
          (isFetching && (
            <div>
              <Flex align="center" gap="middle">
                <Spin indicator={<LoadingOutlined spin />} size="large" />
              </Flex>
            </div>
          ))}
        {messages?.map((m) => {
          const isMe = m.user?.id === user?.id;
          return (
            <div
              key={m.id}
              style={{
                display: "flex",
                justifyContent: isMe ? "flex-end" : "flex-start",
                alignItems: "flex-end",
                marginBottom: 8,
              }}
            >
              {!isMe && (
                <Avatar size={28} style={{ marginRight: 8 }}>
                  {m.user?.name?.charAt(0) ?? "U"}
                </Avatar>
              )}

              <div
                style={{
                  background: isMe ? "#1877F2" : "#dfe1f5",
                  color: isMe ? "#dfe1f5" : "#000000",
                  padding: "8px 12px",
                  borderRadius: isMe
                    ? "18px 18px 4px 18px"
                    : "18px 18px 18px 4px",
                  maxWidth: "70%",
                  boxShadow: isMe
                    ? "0 1px 0 rgba(0,0,0,0.04)"
                    : "0 1px 0 rgba(0,0,0,0.04)",
                  wordBreak: "break-word",
                }}
              >
                <div style={{ fontSize: 14, lineHeight: 1.35 }}>
                  {m.content}
                </div>
              </div>

              {isMe && (
                <Avatar size={28} style={{ marginLeft: 8 }}>
                  {user?.name?.charAt(0) ?? "T"}
                </Avatar>
              )}
            </div>
          );
        })}
        {remoteTypingName && (
          <div style={{ marginTop: 6 }}>
            <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
              <Avatar
                size={18}
                style={{ backgroundColor: "#f0f0f0", color: "#666" }}
              >
                {remoteTypingName.charAt(0)}
              </Avatar>
              <div
                style={{
                  fontSize: 12,
                  color: "#666",
                  display: "flex",
                  alignItems: "center",
                  gap: 8,
                }}
              >
                <span style={{ fontWeight: 500 }}>
                  {remoteTypingName} đang nhắn tin
                </span>
                <div
                  style={{
                    display: "inline-flex",
                    gap: 6,
                    alignItems: "center",
                  }}
                >
                  <span className="typing-dot" />
                  <span className="typing-dot" />
                  <span className="typing-dot" />
                </div>
              </div>
            </div>
            <style>{`
                  .typing-dot{ width:6px; height:6px; border-radius:50%; background:#6b6b6b; opacity:0.25; display:inline-block; transform:translateY(0); }
                  .typing-dot:nth-child(1){ animation: td 1s infinite 0s; }
                  .typing-dot:nth-child(2){ animation: td 1s infinite 0.12s; }
                  .typing-dot:nth-child(3){ animation: td 1s infinite 0.24s; }
                  @keyframes td{ 0%{ opacity:0.25; transform:translateY(0);} 50%{ opacity:1; transform:translateY(-6px);} 100%{ opacity:0.25; transform:translateY(0);} }
                `}</style>
          </div>
        )}
      </div>

      <div
        style={{
          flex: "0 0 auto",
          padding: 12,
          borderTop: "1px solid #f0f0f0",
          background: "#fff",
        }}
      >
        <div style={{ display: "flex", gap: 8 }}>
          <div style={{ flex: 1 }}>
            <Input
              style={{ padding: "5px 25px" }}
              value={messageInput}
              onChange={(e) => onMessageInputChange(e.target.value)}
              onPressEnter={() => handleSendMessage()}
              placeholder="Nhập tin nhắn..."
            />
          </div>
          <Button
            style={{ marginLeft: "5px" }}
            type="primary"
            onClick={() => handleSendMessage()}
          >
            Gửi
          </Button>
        </div>
      </div>
    </div>
  );
};

export default ChatPrivate;
