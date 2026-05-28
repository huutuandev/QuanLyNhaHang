import React, { useEffect, useState } from "react";
import { Table, Button, Space, Popconfirm, message, Modal, Form, Input, Select } from "antd";
import axios from "axios";

const { Option } = Select;

function TableManagement() {
  const [tables, setTables] = useState([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 5,
    total: 0
  });
  const [loading, setLoading] = useState(false);

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [form] = Form.useForm();
  const [editingTable, setEditingTable] = useState(null);

  const token = localStorage.getItem("token");

  const fetchTables = async (page = 1, size = 5) => {
    setLoading(true);
    try {
      const res = await axios.get(`/api/tables?page=${page - 1}&size=${size}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setTables(res.data.content);
      setPagination({
        current: page,
        pageSize: size,
        total: res.data.totalElements
      });
    } catch (err) {
      message.error("Lỗi khi tải danh sách bàn");
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchTables();
  }, []);

  
  const showModal = (record = null) => {
    setEditingTable(record);
    form.setFieldsValue(record || { status: "Available" });
    setIsModalVisible(true);
  };

  const handleSave = async () => {
    try {
      const values = await form.validateFields();
      await axios.post("/api/tables", values, {
        headers: { Authorization: `Bearer ${token}` }
      });
      message.success(editingTable ? "Cập nhật bàn thành công" : "Thêm bàn thành công");
      setIsModalVisible(false);
      fetchTables(pagination.current, pagination.pageSize);
    } catch (err) {
      message.error("Lỗi khi lưu bàn");
    }
  };


  const handleDelete = async (id) => {
    try {
      await axios.delete(`/api/tables/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      message.success("Xóa bàn thành công");
      fetchTables(pagination.current, pagination.pageSize);
    } catch (err) {
      message.error("Lỗi khi xóa bàn");
    }
  };


  const columns = [
    {
      title: "ID",
      dataIndex: "id",
      key: "id"
    },
    {
      title: "Số bàn",
      dataIndex: "tableNumber",
      key: "tableNumber"
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status"
    },
    {
      title: "Hành động",
      key: "action",
      render: (_, record) => (
        <Space>
          <Button onClick={() => showModal(record)}>Sửa</Button>
          <Popconfirm
            title="Bạn có chắc muốn xóa bàn này?"
            onConfirm={() => handleDelete(record.id)}
          >
            <Button danger>Xóa</Button>
          </Popconfirm>
        </Space>
      )
    }
  ];

  return (
    <div style={{ padding: 20 }}>
      <h2>Quản lý bàn</h2>
      <Button type="primary" style={{ marginBottom: 10 }} onClick={() => showModal()}>
        Thêm bàn
      </Button>

      <Table
        columns={columns}
        dataSource={tables}
        rowKey="id"
        loading={loading}
        pagination={{
          current: pagination.current,
          pageSize: pagination.pageSize,
          total: pagination.total,
          onChange: (page, size) => fetchTables(page, size)
        }}
      />

      <Modal
        title={editingTable ? "Sửa bàn" : "Thêm bàn"}
        visible={isModalVisible}
        onOk={handleSave}
        onCancel={() => setIsModalVisible(false)}
      >
        <Form form={form} layout="vertical">
          {editingTable && (
            <Form.Item name="id" style={{ display: "none" }}>
              <Input type="hidden" />
            </Form.Item>
          )}
          <Form.Item
            name="tableNumber"
            label="Số bàn"
            rules={[{ required: true, message: "Vui lòng nhập số bàn" }]}
          >
            <Input type="number" />
          </Form.Item>
          <Form.Item
            name="status"
            label="Trạng thái"
            rules={[{ required: true, message: "Vui lòng chọn trạng thái" }]}
          >
            <Select>
              <Option value="Available">Available</Option>
              <Option value="Reserved">Reserved</Option>
              <Option value="Occupied">Occupied</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default TableManagement;
