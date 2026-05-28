import React, { useEffect, useState } from "react";
import { Table, Button, message, Tag, Space, Dropdown } from "antd";
import axios from "axios";

const ReservationTable = () => {
  const [data, setData] = useState([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 8,
    total: 0
  });
  const [loading, setLoading] = useState(false);
  const [editingRowId, setEditingRowId] = useState(null);

  const token = localStorage.getItem("token");

  const fetchData = async (page, size) => {
    setLoading(true);
    try {
      const res = await axios.get(
        `/api/reservations?page=${page - 1}&size=${size}`,
        {
          headers: { Authorization: `Bearer ${token}` }
        }
      );
      setData(res.data.content);
      setPagination({
        current: page,
        pageSize: size,
        total: res.data.totalElements
      });
    } catch (error) {
      console.error("Error fetching reservations:", error);
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async (id, newStatus) => {
    try {
      await axios.put(
        `/api/reservations/${id}/status`,
        { status: newStatus },
        { headers: { Authorization: `Bearer ${token}` } }
      );
      message.success(`Đã chuyển sang trạng thái ${newStatus}`);
      setEditingRowId(null);
      fetchData(pagination.current, pagination.pageSize);
    } catch (error) {
      message.error("Cập nhật trạng thái thất bại");
    }
  };

  useEffect(() => {
    fetchData(pagination.current, pagination.pageSize);
  }, []);

  const handleTableChange = (pagination) => {
    fetchData(pagination.current, pagination.pageSize);
  };
  const columns = [
    {
      title: "ID",
      dataIndex: "id"
    },
    {
      title:"Người đặt",
      dataIndex:"reservationistName",
      key:"reservationistName"
    },
    {
      title:"Sđt",
      dataIndex:"reservationistPhone",
      key:"reservationistPhone"
    },
    {
      title: "Ngày đặt",
      dataIndex: "reservationDate"
    },
    {
      title: "Giờ đặt",
      dataIndex: "reservationTime"
    },
    {
      title: "Bàn số",
      dataIndex: "tableNumber"
    },
    {
      title: "Số khách",
      dataIndex: "numberOfGuests"
    },
    {
      title: "Ghi chú",
      dataIndex: "note"
    },
    {
      title:"Thanh toán",
      dataIndex:"isPaid",
      key:"isPaid",
      render:(isPaid)=>{
        return isPaid==true?(
          <Tag color="green"> đã thanh toán</Tag>
        ):(
          <Tag color="red"> chưa thanh toán</Tag>
        )
      }
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      render: (status, record) => {
        let color = "blue";
        if (status === "Pending") color = "gold";
        if (status === "Confirmed") color = "green";
        if (status === "Cancelled") color = "red";

        const menuItems = [
          {
            key: "confirmed",
            label: (
              <Button
                type="link"
                onClick={() => updateStatus(record.id, "Confirmed")}
              >
                Confirmed
              </Button>
            )
          },
          {
            key: "pending",
            label: (
              <Button
                type="link"
                onClick={() => updateStatus(record.id, "Pending")}
              >
                Pending
              </Button>
            )
          },
          {
            key: "cancelled",
            label: (
              <Button
                type="link"
                danger
                onClick={() => updateStatus(record.id, "Cancelled")}
              >
                Cancelled
              </Button>
            )
          }
        ];

        return (
          <Dropdown
            menu={{ items: menuItems }}
            trigger={["click"]}
            placement="bottom"
          >
            <Tag
              color={color}
              style={{ cursor: "pointer" }}
              onClick={(e) => e.preventDefault()}
            >
              {status}
            </Tag>
          </Dropdown>
        );
      }
    }
  ];

  return (
    <Table
      rowKey="id"
      columns={columns}
      dataSource={data}
      pagination={{
        ...pagination,
        style: {display: "flex", justifyContent: "center", marginTop: 16 }
      }}
      loading={loading}
      onChange={handleTableChange}
    />
  );
};

export default ReservationTable;
