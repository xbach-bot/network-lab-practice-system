"use client";

import React, { useEffect, useState } from "react";
import { Drawer, Button, List, Avatar, Spin } from "antd";
import { MessageOutlined } from "@ant-design/icons";
import { fetchMyRooms } from "@/config/api";
import ChatPrivate from "@/components/client/Chat/Chat.private";
import { useAppDispatch, useAppSelector } from "@/lib/redux/hooks";
import { IUser } from "@/types/backend";
import { setChatTarget, setChatVisible } from "@/lib/redux/slice/chat.slice";



const RoomsDrawer: React.FC = () => {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [rooms, setRooms] = useState<any[]>([]);
  const pathName = window.location.pathname;

  const user = useAppSelector((s) => s.auth.user);
  const chatVisible = useAppSelector((s) => s.chat.chatVisible);
  const chatTarget = useAppSelector((s) => s.chat.chatTarget);
  const dispatch = useAppDispatch();
  useEffect(() => {
    if(pathName == "/ranking") {
      dispatch(setChatVisible(false));
      setOpen(false);
    }
  },[pathName])

  const loadRooms = async () => {
    setLoading(true);
    try {
      const res = await fetchMyRooms();
      if (res && (res.status === 200 || res.status === 201)) {
        const data = res.data || [];
        setRooms(data);
      } else {
        setRooms([]);
      }
    } catch (e) {
      console.error(e);
      setRooms([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (open) {
      void loadRooms();
    }
  }, [open]);

  const openChatForRoom = (room: any) => {
    if (!user) return;
    const other = (room.participants || []).find((p: any) => p.id !== user.id) || null;
    dispatch(setChatTarget(other as IUser));
    dispatch(setChatVisible(true));
    //
    setOpen(false);
  };

  return (
    <>
      <Button
        type="text"
        onClick={() => setOpen(true)}
        style={{ marginRight: 8 }}
        aria-label="Open chats"
      >
        <MessageOutlined style={{ fontSize: 20 }} />
      </Button>

      <Drawer
        title="Tin nhắn"
        placement="right"
        onClose={() => setOpen(false)}
        open={open}
        width={360}
      >
        {loading ? (
          <div style={{ textAlign: "center", padding: 24 }}>
            <Spin />
          </div>
        ) : (
          <List
            itemLayout="horizontal"
            dataSource={rooms}
            renderItem={(room) => {
              const other = (room.participants || []).find((p: any) => p.id !== user?.id) || room.participants?.[0];
              const initials = other?.name ? other.name.substring(0, 1).toUpperCase() : "U";
              return (
                <List.Item style={{ cursor: "pointer" }} onClick={() => openChatForRoom(room)}>
                  <List.Item.Meta
                    avatar={<Avatar>{initials}</Avatar>}
                    title={other?.name ?? room.name}
                    
                  />
                </List.Item>
              );
            }}
          />
        )}
      </Drawer>

      {chatVisible && chatTarget && pathName !== "/ranking" && (
        <ChatPrivate
          chatTarget={chatTarget as any}
        />
      )}
    </>
  );
};

export default RoomsDrawer;
