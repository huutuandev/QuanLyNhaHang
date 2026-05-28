import React, { useEffect, useState } from "react";
import { Form, Input, Button, message, Typography } from "antd";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom";

const { Title } = Typography;

function EditPost() {
  const navigate = useNavigate();
  const location = useLocation();
  const postId = location.state?.id || null;
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const token = localStorage.getItem("token");

  useEffect(() => {
    if (postId) {
      axios
        .get(`/api/posts/${postId}`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        .then((res) => {
          const data = res.data;
          form.setFieldsValue({
            title: data.title,
            content: data.content,
            imageUrl: data.imageUrl,
          });
        })
        .catch(() => {
          message.error("Không thể tải thông tin bài viết.");
        });
    }
  }, [postId, form, token]);

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const payload = postId ? { ...values, id: parseInt(postId) } : values;
      await axios.post("/api/posts", payload, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });
      message.success(postId ? "Cập nhật bài viết thành công!" : "Thêm bài viết thành công!");
      navigate("/admin/Post-Manager");
    } catch (error) {
      message.error(postId ? "Cập nhật bài viết thất bại!" : "Thêm bài viết thất bại!");
    }
    setLoading(false);
  };

  return (
    <div className="add-post-container">
      <Title level={2} className="add-post-title">
        {postId ? "Chỉnh sửa bài viết" : "Thêm bài viết mới"}
      </Title>
      <Form
        layout="vertical"
        onFinish={onFinish}
        form={form}
        className="add-post-form"
      >
        <Form.Item
          label="Tiêu đề"
          name="title"
          rules={[{ required: true, message: "Vui lòng nhập tiêu đề!" }]}
        >
          <Input placeholder="Nhập tiêu đề bài viết" />
        </Form.Item>

        <Form.Item
          label="Nội dung"
          name="content"
          rules={[{ required: true, message: "Vui lòng nhập nội dung!" }]}
        >
          <Input.TextArea rows={5} placeholder="Nhập nội dung bài viết" />
        </Form.Item>

        <Form.Item
          label="URL ảnh"
          name="imageUrl"
          rules={[{ required: true, message: "Vui lòng nhập URL ảnh!" }]}
        >
          <Input placeholder="Nhập URL ảnh" />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" loading={loading}>
            {postId ? "Cập nhật" : "Thêm"}
          </Button>
          <Button
            style={{ marginLeft: "10px" }}
            onClick={() => navigate("/admin/Post-Manager")}
          >
            Hủy
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}

export default EditPost;
