"use client";
import React, { useEffect, useState } from "react";
import classNames from "classnames/bind";
import styles from "../styles/Home.module.scss";

const cx = classNames.bind(styles);

export default function Home() {
  return (
    <div className={cx("wrapper")}>
      <div className={cx("container")}>
        <h1>Welcome to the Website of PTIT</h1>
      </div>
    </div>
  );
}
