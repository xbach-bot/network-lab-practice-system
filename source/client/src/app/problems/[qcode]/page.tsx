"use client";

import React, { useCallback, useEffect, useState } from "react";
import { useParams, useRouter } from "next/navigation";
import { fetchProblemByQCode, fetchSubmissions } from "@/config/api";
import {
  Card,
  Typography,
  Spin,
  Table,
  Tag,
  Space,
  Button,
  notification,
  Pagination,
} from "antd";
import type { ColumnsType } from "antd/es/table";
import { IProblem, IModelPaginate, ISubmission } from "@/types/backend";
import { useAppSelector } from "@/lib/redux/hooks";
import styles from "@/styles/Problem.module.scss";
import classNames from "classnames/bind";
import socket from "@/utils/socket";
import dayjs from "dayjs";

const cx = classNames.bind(styles);

const { Title, Paragraph, Text } = Typography;

type SubmissionRow = {
  id: number;
  inputData: string;
  studentResult: string;
  expectedResult: string;
  correct: boolean;
  createdAt: string;
  status: string;
  user: { id: number; name: string; studentId?: string; email?: string };
};

export default function Page() {
  const params = useParams();
  const qcode = (params as any)?.qcode as string | undefined;

  const [problem, setProblem] = useState<IProblem | null>(null);
  const [loadingProblem, setLoadingProblem] = useState(false);

  const [submissions, setSubmissions] = useState<SubmissionRow[]>([]);
  const [loadingSubmissions, setLoadingSubmissions] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(20);
  const [total, setTotal] = useState(0);
  const router = useRouter();

  const user = useAppSelector((state) => state.auth.user.id);
  const loading = useAppSelector((state) => state.auth.isLoading);

  const loadProblem = useCallback(async (q?: string) => {
    if (!q) return;
    setLoadingProblem(true);
    try {
      const res = await fetchProblemByQCode(q);
      if (!res) {
        setProblem(null);
        return;
      }
      if (res.status !== 200) {
        setProblem(null);
        return;
      }
      setProblem(res.data as IProblem);
    } catch (err: any) {
      return;
    } finally {
      setLoadingProblem(false);
    }
  }, []);

  const loadSubmissions = useCallback(async (q?: string, p = 1, size = 20) => {
    if (!q) return;
    setLoadingSubmissions(true);
    try {
      const res = await fetchSubmissions({ page: p, size, qCode: q });
      if (!res) {
        notification.error({ message: "Không nhận được phản hồi từ server" });
        setSubmissions([]);
        setTotal(0);
        return;
      }
      if (res.status !== 200) {
        setSubmissions([]);
        setTotal(0);
        return;
      }

      const meta = (res.data as IModelPaginate<any>).meta;
      const result = (res.data as IModelPaginate<any>).result || [];
      setSubmissions(result as SubmissionRow[]);
      setTotal(meta?.total ?? result.length ?? 0);
      setPage(meta?.current ?? p);
      setPageSize(meta?.pageSize ?? size);
    } catch (err: any) {
      return;
    } finally {
      setLoadingSubmissions(false);
    }
  }, []);

  useEffect(() => {
    if (socket) {
      socket.on("submission_created", (res: string) => {
        const data = JSON.parse(res) as ISubmission;

        if (data.problem?.qcode !== qcode) return;

        const newRow: SubmissionRow = {
          id: data.id,
          inputData: (data.inputData ?? "") as string,
          studentResult: (data.studentResult ?? "") as string,
          expectedResult: (data.expectedResult ?? "") as string,
          correct: !!data.correct,
          createdAt: new Date().toISOString(),
          status: data.status ?? "",
          user: {
            id: data.user?.id ?? 0,
            name: data.user?.name ?? "",
            studentId: data.user?.studentId,
            email: data.user?.email,
          },
        };
        setSubmissions((prevSubmissions) => [newRow, ...prevSubmissions]);
        setTotal((prevTotal) => prevTotal + 1);

        setProblem((prevProblem) => {
          if (!prevProblem) return prevProblem;
          return {
            ...prevProblem,
            solved:
              data.correct && prevProblem.solved === false
                ? true
                : prevProblem.solved,
          };
        });
      });
    }

    
    return () => {
      if (socket) socket.off("submission_updated");
    };
  }, [socket]);


  useEffect(() => {
    if (!qcode) return;
    if(loading) return;

    if(!user) {
      router.push("/login");
      return;
    }
    loadProblem(qcode);
    loadSubmissions(qcode, 1, pageSize);
  }, [qcode, loadProblem, loading, user]);

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
      render: (val: string) => dayjs(val).format("HH:mm DD/MM/YYYY"),
    },
  ];

  return (
    <div className={cx("wrapper")} style={{ padding: 16 }}>
      <Spin spinning={loadingProblem}>
        <Card>
          <Space direction="vertical" style={{ width: "100%" }}>
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <div>
                <Title level={4} style={{ margin: 0 }}>
                  {problem?.title +
                    (problem?.qcode ? " (" + problem?.qcode + ")" : "")}
                </Title>
                <div style={{ marginTop: 6 }}>
                  <Tag>{problem?.protocolType ?? "-"}</Tag>
                  <Tag>{problem?.type ?? "-"}</Tag>
                  {problem?.ioType && <Tag>{problem.ioType}</Tag>}
                  {typeof problem?.solved === "boolean" && (
                    <Tag
                      color={problem.solved ? "green" : "default"}
                      style={{ marginLeft: 8 }}
                    >
                      {problem.solved ? "Đã giải" : "Chưa giải"}
                    </Tag>
                  )}
                </div>
              </div>
            </div>

            <div>
              <div
                style={{
                  whiteSpace: "pre-wrap",
                  background: "#ffffff",
                  padding: 16,
                  borderRadius: 8,
                  boxShadow: "0 1px 3px rgba(16,24,40,0.05)",
                  border: "1px solid #f0f0f0",
                  color: "rgba(0,0,0,0.85)",
                  lineHeight: 1.6,
                }}
              >
                <Paragraph style={{ margin: 0 }}>
                  {problem?.description ?? "Không có mô tả"}
                </Paragraph>
              </div>
            </div>
          </Space>
        </Card>
      </Spin>

      <div style={{ marginTop: 16 }}>
        <Card title={`Các bài nộp cho đề ${qcode ?? ""}`}>
          <Spin spinning={loadingSubmissions}>
            <Table
              columns={columns}
              dataSource={submissions}
              rowKey={(r) => r.id}
              pagination={false}
              bordered
            />

            <div style={{ marginTop: 12, textAlign: "right" }}>
              <Pagination
                current={page}
                pageSize={pageSize}
                total={total}
                onChange={(p, ps) => {
                  setPage(p);
                  setPageSize(ps);
                  void loadSubmissions(qcode, p, ps);
                }}
                showSizeChanger
                pageSizeOptions={[10, 20, 50]}
              />
            </div>
          </Spin>
        </Card>
      </div>
    </div>
  );
}
