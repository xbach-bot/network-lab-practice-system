"use client";

import React, { useState } from "react";
import classNames from "classnames/bind";
import styles from "@/styles/Register.module.scss";
import {
  Button,
  Divider,
  Form,
  Input,
  Select,
  message,
  notification,
} from "antd";
import { IUser } from "@/types/backend";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { callRegister } from "@/config/api";

const cx = classNames.bind(styles);

const Register = () => {
  const [isSubmit, setIsSubmit] = useState<boolean>(false);
  const navigate = useRouter();

  const handleSubmit = async (values: IUser) => {
    setIsSubmit(true);
    const res = await callRegister(values);
    setIsSubmit(false);

    if (res.status == 200) {
      message.success("Đăng ký tài khoản thành công!");
      navigate.push("/login");
    } else {
      notification.error({
        message: "Đăng ký tài khoản thất bại!",
        description: res.error,
      });
    }
  };

  return (
    <div className={cx("wrapper")}>
      <div className={cx("container")}>
        <div className={cx("heading")}>
          <h2> Đăng Ký Tài Khoản </h2>
          <Divider />
        </div>
        <Form<IUser> name="basic" onFinish={handleSubmit} autoComplete="off">
          <Form.Item
            labelCol={{ span: 24 }}
            label="Họ tên"
            name="name"
            rules={[{ required: true, message: "Họ tên không được để trống!" }]}
          >
            <Input />
          </Form.Item>

          <Form.Item
            labelCol={{ span: 24 }}
            label="Email"
            name="email"
            rules={[{ required: true, message: "Email không được để trống!" }]}
          >
            <Input type="email" />
          </Form.Item>

          <Form.Item
            labelCol={{ span: 24 }}
            label="Mật khẩu"
            name="password"
            rules={[
              { required: true, message: "Mật khẩu không được để trống!" },
            ]}
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            labelCol={{ span: 24 }}
            label="Mã sinh viên"
            name="studentId"
            rules={[
              {
                required: true,
                message: "Mã sinh viên không được để trống!",
              },
            ]}
          >
            <Input type="number" />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" loading={isSubmit}>
              Đăng ký
            </Button>
          </Form.Item>
          <Divider> Or </Divider>
          <p className="text text-normal">
            {" "}
            Đã có tài khoản ?
            <span>
              <Link href="/login"> Đăng Nhập </Link>
            </span>
          </p>
        </Form>
      </div>
    </div>
  );
};

export default Register;
