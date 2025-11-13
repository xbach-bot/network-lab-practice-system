"use client";

import React, { useCallback, useEffect, useState } from "react";
import { fetchSubmissions } from "@/config/api";
import {
  Table,
  Space,
  Button,
  Tag,
  Spin,
  notification,
  Pagination,
  Typography,
  Card,
} from "antd";
import type { ColumnsType } from "antd/es/table";
import { IModelPaginate } from "@/types/backend";
import { useAppSelector } from "@/lib/redux/hooks";
import { useRouter } from "next/navigation";

const { Text } = Typography;

type SubmissionRow = {
  id: number;
  inputData: string;
  studentResult: string;
  expectedResult: string;
  correct: boolean;
  createdAt: string;
  status: string;
  user: { id: number; name: string; studentId?: string };
  problem: { id: number; title: string; qcode?: string };
};

export default function Page() {
  const [data, setData] = useState<SubmissionRow[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [total, setTotal] = useState(0);

  const user = useAppSelector((state) => state.auth.user.id);

  const authLoading = useAppSelector((state) => state.auth.isLoading);

  const navigate = useRouter();

  const load = useCallback(async (p = 1, size = 20) => {
    setLoading(true);
    try {
      const res = await fetchSubmissions({ page: p, size });
      if (!res) {
        notification.error({ message: "Không nhận được phản hồi từ server" });
        setData([]);
        setTotal(0);
        return;
      }
      if (res.status !== 200) {
        notification.error({
          message: res.message || "Lỗi khi lấy submissions",
        });
        setData([]);
        setTotal(0);
        return;
      }

      const meta = (res.data as IModelPaginate<any>).meta;
      const result = (res.data as IModelPaginate<any>).result || [];
      setData(result as SubmissionRow[]);
      setTotal(meta?.total ?? result.length ?? 0);
      setPage(meta?.current ?? p);
      setPageSize(meta?.pageSize ?? size);
    } catch (err: any) {
      notification.error({
        message: "Lỗi hệ thống",
        description: err?.message,
      });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (authLoading) return;

    if (!user) {
      navigate.push("/login");
      return;
    }

    void load(1, pageSize);
  }, [load, authLoading, user]);

  const columns: ColumnsType<SubmissionRow> = [
    { title: "ID", dataIndex: "id", key: "id", width: 70 },
    {
      title: "Sinh viên",
      key: "student",
      render: (_, row) => (
        <div>
          <Text strong>{row.user?.name}</Text>
          <div style={{ color: "var(--neutral-500)" }}>
            {row.user?.studentId}
          </div>
        </div>
      ),
    },
    {
      title: "Mã bài",
      dataIndex: ["problem", "qcode"],
      key: "problemQcode",
    },
    {
      title: "Input",
      dataIndex: "inputData",
      key: "inputData",
    },
    {
      title: "Student result",
      dataIndex: "studentResult",
      key: "studentResult",
    },
    {
      title: "Expected",
      dataIndex: "expectedResult",
      key: "expectedResult",
    },
    {
      title: "Kết quả",
      dataIndex: "correct",
      key: "correct",
      width: 120,
      render: (val: boolean) =>
        val ? <Tag color="success">Đúng</Tag> : <Tag color="error">Sai</Tag>,
    },
    { title: "Trạng thái", dataIndex: "status", key: "status", width: 120 },
    {
      title: "Thời gian nộp",
      dataIndex: "createdAt",
      key: "createdAt",
      width: 180,
      render: (val: string) => new Date(val).toLocaleString("vi-VN"),
    },
  ];

  return (
    <div style={{ padding: 16 }}>
      <Card title="Tất cả submissions">
        <Spin spinning={loading}>
          <Table
            columns={columns}
            dataSource={data}
            rowKey={(r) => r.id}
            bordered
            pagination={false}
          />

          <div style={{ marginTop: 12, textAlign: "right" }}>
            <Pagination
              current={page}
              pageSize={pageSize}
              total={total}
              onChange={(p, ps) => {
                setPage(p);
                setPageSize(ps);
                void load(p, ps);
              }}
              showSizeChanger
              pageSizeOptions={[10, 20, 50]}
            />
          </div>
        </Spin>
      </Card>
    </div>
  );
}
