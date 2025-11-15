"use client";
import React, { useEffect, useRef, useState } from "react";
import classnames from "classnames/bind";
import styles from "../../../styles/Header.module.scss";
import Link from "next/link";
import { useAppDispatch, useAppSelector } from "@/lib/redux/hooks";
import { Avatar, Dropdown, Skeleton, Space, notification } from "antd";

import { setLogoutAction } from "@/lib/redux/slice/auth.slice";
import { logout } from "@/config/api";
import { IMeta } from "@/types/backend";
const cx = classnames.bind(styles);

const Header: React.FC = () => {
  const isAuth = useAppSelector((state) => state?.auth.isAuthenticated);
  const user = useAppSelector((state) => state?.auth.user);
  const [open, setOpen] = useState<boolean>(false);
  const loading = useAppSelector((state) => state?.auth.isLoading);
  const [api, contextHolder] = notification.useNotification();
  const [meta, setMeta] = useState<IMeta>();

  const dispatch = useAppDispatch();
  const handleLogout = async () => {
    const data = await logout();

    dispatch(setLogoutAction({}));
  };

  return (
    <div className={cx("wrapper")}>
      <div className={cx("container")}>
        <Link href="/">
          <div className={cx("header-logo")}>Trang chủ</div>
        </Link>
        <div className={cx("header-left")}>
          <div className={cx("header-item")}>
            <Link href="/submissions">Log hệ thống</Link>
          </div>
          <div className={cx("header-item")}>
            <Link href="/ranking">Bảng xếp hạng</Link>
          </div>
          <div className={cx("header-item")}>
            <Link href="/submission-file">File code đã nộp</Link>
          </div>
        </div>

        {loading ? (
          <Skeleton.Avatar style={{ width: "50px", height: "50px" }} active />
        ) : (
          <div className={cx("header-right")}>
            {isAuth ? (
              <div className={cx("right-items")}>
                <Dropdown
                  menu={{
                    items: [{ label: "Đăng xuất", key: "logout" }],
                    onClick: (e) => {
                      if (e.key === "logout") {
                        handleLogout();
                      }
                    },
                  }}
                >
                  <Space style={{ cursor: "pointer" }}>
                    <span>Xin chào {user?.name}</span>
                    <Avatar>
                      {" "}
                      {user?.name?.substring(0, 2)?.toUpperCase()}{" "}
                    </Avatar>
                  </Space>
                </Dropdown>
              </div>
            ) : (
              <>
                <div className={cx("header-item")}>
                  <Link href="/login">ĐĂNG NHẬP</Link>
                </div>
                <div className={cx("header-item")}>
                  <Link href="/register">ĐĂNG KÝ</Link>
                </div>
              </>
            )}
          </div>
        )}
      </div>
      <>{contextHolder}</>
    </div>
  );
};

export default Header;
