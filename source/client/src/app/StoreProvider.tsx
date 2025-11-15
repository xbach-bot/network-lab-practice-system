"use client";
import { useEffect, useRef } from "react";
import { Provider } from "react-redux";
import { makeStore, AppStore } from "@/lib/redux/store";
import socket from "@/utils/socket";
import { setChatTarget, setChatVisible } from "@/lib/redux/slice/chat.slice";

export default function StoreProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const storeRef = useRef<AppStore>();
  if (!storeRef.current) {
    
    storeRef.current = makeStore();
  }

  
  useEffect(() => {
    const handler = (payload: any) => {
      try {
        if (!payload || !payload.user) return;
        const state = storeRef.current!.getState();
        const me = state.auth.user;
        
        if (!me || payload.user.id === me.id) return;

       
        storeRef.current!.dispatch(setChatTarget(payload.user));
        storeRef.current!.dispatch(setChatVisible(true));
      } catch (e) {
      
      }
    };

    socket.on("messagePrivate", handler);
    return () => {
      socket.off("messagePrivate", handler);
    };
  }, []);

  return <Provider store={storeRef.current}>{children}</Provider>;
}
