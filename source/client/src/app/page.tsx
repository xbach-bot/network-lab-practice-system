"use client";

import React, { useEffect, useMemo, useState } from "react";
import classNames from "classnames/bind";
import styles from "../styles/Home.module.scss";
import { useRouter } from "next/navigation";
import { IBackendRes, IModelPaginate, IProblem } from "@/types/backend";
import { TablePaginationConfig } from "antd/lib";
import type { ColumnsType } from "antd/es/table";
import { FilterValue, SorterResult } from "antd/es/table/interface";
import {
  Tooltip,
  Tag,
  Space,
  Button,
  notification,
  Input,
  Spin,
  Table,
} from "antd";
import { fetchProblems } from "@/config/api";
import { SearchOutlined, ReloadOutlined } from "@ant-design/icons";
import DebounceInput from "@/hooks/debounce.input";
import { useAppSelector } from "@/lib/redux/hooks";

const cx = classNames.bind(styles);

export default function Home() {
  const router = useRouter();

  const [data, setData] = useState<IProblem[]>([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(6);
  const [total, setTotal] = useState(0);
  const [search, setSearch] = useState("");
  const user = useAppSelector((state) => state.auth.user.id);

  const authLoading = useAppSelector((state) => state.auth.isLoading);

  const debounceSearch = DebounceInput(search, 500);

  const load = async (p = page, ps = pageSize, name = debounceSearch) => {
    try {
      setLoading(true);
      const res = await fetchProblems({ page: p, size: ps, name });
      if (!res) {
        notification.error({ message: "Lỗi khi tải dữ liệu" });
        return;
      }

      const backendData = res as unknown as IBackendRes<
        IModelPaginate<IProblem>
      >;
      const meta = backendData?.data?.meta;
      const result = backendData?.data?.result || [];

      setData(result.map((r) => ({ ...r, key: r.id })));
      setPage(meta?.current || p);
      setPageSize(meta?.pageSize || ps);
      setTotal(meta?.total ?? result.length);
    } catch (err: any) {
      notification.error({
        message: "Lỗi hệ thống",
        description: err?.message,
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (authLoading) return;

    if (!user) {
      router.push("/login");
      return;
    }

    load(page, pageSize, debounceSearch);
  }, [debounceSearch, authLoading, user]);

  const handleTableChange = (
    pagination: TablePaginationConfig,
    _filters: Record<string, FilterValue | null>,
    _sorter: SorterResult<IProblem> | SorterResult<IProblem>[]
  ) => {
    const cur = pagination.current || 1;
    const ps = pagination.pageSize || pageSize;
    load(cur, ps, debounceSearch);
  };
  const columns: ColumnsType<IProblem> = [
    {
      title: "ID",
      dataIndex: "id",
      width: 100,
    },
    {
      title: "Title",
      dataIndex: "title",
      render: (text: string) => (
        <Tooltip title={text}>
          <div
            style={{
              maxWidth: 600,
              whiteSpace: "nowrap",
              overflow: "hidden",
              textOverflow: "ellipsis",
            }}
          >
            {text}
          </div>
        </Tooltip>
      ),
    },
    {
      title: "QCode",
      dataIndex: "qcode",
      width: 160,
      render: (q) => <code>{q}</code>,
    },
    {
      title: "Protocol",
      dataIndex: "protocolType",
      width: 140,
      render: (p) => (p ? <Tag>{p.toUpperCase()}</Tag> : null),
    },
    {
      title: "Status",
      dataIndex: "solved",
      width: 140,
      render: (s: boolean) =>
        s ? (
          <Tag color="success">Đã hoàn thành</Tag>
        ) : (
          <Tag>Chưa hoàn thành</Tag>
        ),
    },
    {
      title: "Actions",
      key: "actions",
      width: 140,
      render: (_: any, record: IProblem) => (
        <Space>
          <Button
            type="primary"
            size="small"
            onClick={() => {
              if (!record.qcode) {
                notification.warning({ message: "Không có qCode cho bài này" });
                return;
              }
              // navigate to /problems/{qcode}
              router.push(`/problems/${record.qcode}`);
            }}
          >
            Làm bài
          </Button>
        </Space>
      ),
    },
  ];

  const pagination = useMemo(
    () => ({ current: page, pageSize, total }),
    [page, pageSize, total]
  );

  return (
    <div className={cx("wrapper")} style={{ padding: 24 }}>
      <div className={cx("container")}>
        <Space direction="vertical" size={16} style={{ width: "100%" }}>
          <h1>Danh sách bài tập</h1>

          <Space
            style={{
              display: "flex",
              justifyContent: "space-between",
              width: "100%",
            }}
          >
            <Input
              placeholder="Search by name / title"
              prefix={<SearchOutlined />}
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              allowClear
              style={{ maxWidth: 420 }}
            />

            <Space>
              <Button
                icon={<ReloadOutlined />}
                onClick={() => load(1, pageSize, debounceSearch)}
              >
                Refresh
              </Button>
            </Space>
          </Space>

          <Spin spinning={loading}>
            <Table
              columns={columns}
              dataSource={data}
              pagination={pagination}
              onChange={handleTableChange}
              rowKey={(record) => record.id}
              bordered
              rowClassName={(record) => (record?.solved ? "solved-row" : "")}
            />
          </Spin>
        </Space>
      </div>
    </div>
  );
}
