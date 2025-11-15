"use client";

import React, { useCallback, useEffect, useState, useRef } from "react";
import {
  fetchUserRanking,
  fetchMyRooms,
  createPrivateRoom,
  createChat,
  fetchChats,
} from "@/config/api";
import {
  Card,
  Row,
  Col,
  Table,
  Pagination,
  Spin,
  Typography,
  Button,
  Avatar,
  Input,
  message as antdMessage,
} from "antd";
import { CrownOutlined } from "@ant-design/icons";
import { CloseOutlined } from "@ant-design/icons";
import type { ColumnsType } from "antd/es/table";
import type { IUserRank } from "@/types/backend";
import { useAppSelector } from "@/lib/redux/hooks";
import { useRouter } from "next/navigation";
import ChatPrivate from "@/components/client/Chat/Chat.private";

const { Title } = Typography;

export default function Page() {
  const [topList, setTopList] = useState<IUserRank[]>([]);
  const [tableData, setTableData] = useState<IUserRank[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [chatVisible, setChatVisible] = useState(false);
  const [chatTarget, setChatTarget] = useState<any | null>(null);
  const [loadingRoom, setLoadingRoom] = useState(false);

  const user = useAppSelector((state) => state.auth.user);

  const isLoadingAuth = useAppSelector((state) => state.auth.isLoading);

  const navigate = useRouter();

  const loadData = useCallback(async (p: number, size: number) => {
    setLoading(true);
    try {
      const res = await fetchUserRanking({ page: p, size });
      if (res && res.status === 200) {
        const result = res.data?.result || [];
        const meta = res.data?.meta;

        setTableData(result);
        setTotal(meta?.total ?? result.length ?? 0);
        setPage(meta?.current ?? p);
        setPageSize(meta?.pageSize ?? size);
        
        if (p === 1) {
          const topRes = await fetchUserRanking({ page: 1, size: 3 });
          if (topRes && topRes.status === 200) {
            setTopList(topRes.data?.result || []);
          }
        }
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (isLoadingAuth) return;

    if (!user.id) {
      navigate.push("/login");
      return;
    }

    void loadData(1, pageSize);
  }, [loadData, pageSize, isLoadingAuth]);

  const handleChangePage = (p: number, ps: number) => {
    void loadData(p, ps);
  };

  const handleOpenChat = async (targetUser: any) => {
    setChatTarget(targetUser);
    setLoadingRoom(true);
    setChatVisible(true);
  };

  const columns: ColumnsType<IUserRank> = [
    {
      title: "STT",
      key: "index",
      width: 70,
      render: (_, __, idx) => (page - 1) * pageSize + idx + 1,
    },
    {
      title: "Mã sinh viên",
      dataIndex: ["user", "studentId"],
      key: "studentId",
    },
    { title: "Họ và tên", dataIndex: ["user", "name"], key: "name" },
    {
      title: "Làm đúng",
      dataIndex: "correctSubmissions",
      key: "correct",
      width: 120,
    },
    {
      title: "Đã nộp",
      dataIndex: "totalSubmissions",
      key: "total",
      width: 120,
    },
    {
      title: "",
      key: "action",
      width: 120,
      render: (_text, record) => {
        return (
          user.id !== record.user.id && (
            <Button
              type="primary"
              size="small"
              onClick={() => handleOpenChat(record.user)}
            >
              Nhắn tin
            </Button>
          )
        );
      },
    },
  ];

  const displayTop = [
    topList[0] ?? null,
    topList[1] ?? null,
    topList[2] ?? null,
  ];

  return (
    <div style={{ padding: 16 }}>
      <Spin spinning={loading}>
        <div style={{ marginBottom: 24 }}>
          <Row gutter={24} justify="center">
            {displayTop.map((u, i) => {
              const rank = i + 1;
              const bg =
                rank === 1 ? "#fff7b8" : rank === 2 ? "#f0f2f5" : "#fde3b7";
              return (
                <Col key={i} xs={24} sm={8} md={6} lg={6}>
                  <Card
                    style={{
                      width: 320,
                      background: bg,
                      borderRadius: 8,
                      textAlign: "center",
                    }}
                  >
                    <div
                      style={{
                        fontSize: 36,
                        color: rank === 1 ? "#fadb14" : undefined,
                      }}
                    >
                      <CrownOutlined />
                    </div>

                    <Title level={5} style={{ marginTop: 8 }}>
                      {u?.user?.name ?? "-"}
                    </Title>
                    <div style={{ color: "rgba(0,0,0,0.45)" }}>
                      {u?.user?.studentId ?? "-"}
                    </div>

                    <Row style={{ marginTop: 12 }}>
                      <Col span={12}>
                        <div style={{ color: "#39c26a", fontSize: 20 }}>
                          {u?.correctSubmissions ?? 0}
                        </div>
                        <div style={{ color: "rgba(0,0,0,0.45)" }}>
                          Làm đúng
                        </div>
                      </Col>
                      <Col span={12}>
                        <div style={{ color: "#1890ff", fontSize: 20 }}>
                          {u?.totalSubmissions ?? 0}
                        </div>
                        <div style={{ color: "rgba(0,0,0,0.45)" }}>Đã nộp</div>
                      </Col>
                    </Row>
                  </Card>
                </Col>
              );
            })}
          </Row>
        </div>

        {/* Table */}
        <Card title="Danh sách xếp hạng">
          <Table<IUserRank>
            columns={columns}
            dataSource={tableData}
            rowKey={(r) => r.user.id}
            pagination={false}
            bordered
          />

          <div style={{ marginTop: 12, textAlign: "right" }}>
            <Pagination
              current={page}
              pageSize={pageSize}
              total={total}
              onChange={handleChangePage}
              showSizeChanger
              pageSizeOptions={[10, 20, 50]}
            />
          </div>
        </Card>
        
        {chatVisible && (
          <ChatPrivate
            chatTarget={chatTarget}
            loadingRoom={loadingRoom}
            setChatVisible={setChatVisible}
          />
        )}
      </Spin>
    </div>
  );
}
