"use client";

import React, { useCallback, useEffect, useState } from "react";
import { Card, Table, Button, Modal, Spin, notification, Tag } from "antd";
import dayjs from "dayjs";
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter";
import { vscDarkPlus } from "react-syntax-highlighter/dist/esm/styles/prism";
import {
  fetchContentOfSubmissionFile,
  fetchSubmissionFile,
} from "@/config/api";
import { ISubmissionFile } from "@/types/backend";
import { useAppSelector } from "@/lib/redux/hooks";
import { useRouter } from "next/navigation";

export default function SubmissionFilePage() {
  const [loading, setLoading] = useState(false);
  const [files, setFiles] = useState<ISubmissionFile[]>([]);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [total, setTotal] = useState(0);

  const [viewing, setViewing] = useState<{
    id: number;
    content: string | null;
  } | null>(null);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalLoading, setModalLoading] = useState(false);

  const navigate = useRouter();

  const isLoading = useAppSelector((state) => state.auth.isLoading);

  const user = useAppSelector((state) => state.auth.user.id);

  const loadFiles = useCallback(async (p = 1, size = 20) => {
    setLoading(true);
    try {
      const res = await fetchSubmissionFile();
      if (!res || (res as any).status !== 200) {
        setFiles([]);
        setTotal(0);
        return;
      }
      setFiles(res.data?.result ?? []);
      setTotal(res.data?.meta?.total ?? res.data?.result?.length ?? 0);
      setPage(p);
      setPageSize(size);
    } catch (err: any) {
      notification.error({ message: "Không lấy được danh sách" });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isLoading) return;

    if (!user) {
      navigate.push("/login");
      return;
    }
    void loadFiles();
  }, [loadFiles, isLoading, user]);

  const openFile = async (id?: number) => {
    if (!id) return;
    setModalLoading(true);
    setModalOpen(true);
    setViewing({ id, content: null });
    try {
      const res = await fetchContentOfSubmissionFile(id);

      setViewing({ id, content: res.data?.message as string });
    } catch (err: any) {
      notification.error({
        message: "Lỗi khi tải nội dung file",
        description: err?.message,
      });
      setModalOpen(false);
    } finally {
      setModalLoading(false);
    }
  };

  const columns = [
    { title: "#", dataIndex: "id", key: "id", width: 70 },
    { title: "Problem", dataIndex: ["problem", "title"], key: "title" },
    {
      title: "QCode",
      dataIndex: ["problem", "qcode"],
      key: "qcode",
      width: 140,
    },
    {
      title: "Thời gian nộp",
      dataIndex: "createdAt",
      key: "createdAt",
      width: 200,
      render: (val: string) =>
        val ? dayjs(val).format("HH:mm DD/MM/YYYY") : "-",
    },
    {
      title: "Trạng thái",
      dataIndex: ["problem", "solved"],
      key: "solved",
      width: 200,
      render: (val: boolean) =>
        val ? <Tag color="success">Đã có log xử lý đúng</Tag> : <Tag color="error">Sai</Tag>,
    },
    {
      title: "Hành động",
      key: "actions",
      width: 140,
      render: (_: any, record: ISubmissionFile) => (
        <Button
          type="link"
          onClick={() => void openFile(record.id as unknown as number)}
        >
          Xem
        </Button>
      ),
    },
  ];

  return (
    <div style={{ padding: 16 }}>
      <Card title="Các file đã nộp">
        <Spin spinning={loading}>
          <Table
            rowKey={(r: any) => r.id}
            columns={columns}
            dataSource={files}
            pagination={{
              current: page,
              pageSize,
              total,
              onChange: (p, ps) => {
                setPage(p);
                setPageSize(ps);
                void loadFiles(p, ps);
              },
            }}
          />
        </Spin>
      </Card>

      <Modal
        title={"Xem mã nguồn"}
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        footer={null}
        width={900}
      >
        {modalLoading || !viewing ? (
          <div style={{ textAlign: "center", padding: 32 }}>
            <Spin />
          </div>
        ) : (
          <div style={{ maxHeight: "70vh", overflow: "auto" }}>
            <SyntaxHighlighter
              language="java"
              style={vscDarkPlus}
              showLineNumbers
            >
              {viewing.content ?? ""}
            </SyntaxHighlighter>
          </div>
        )}
      </Modal>
    </div>
  );
}
