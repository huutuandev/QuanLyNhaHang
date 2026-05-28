import { useNavigate } from "react-router-dom";
import { Form, Input, Button, Select, message } from "antd";
import axios from "axios";

const { Option } = Select;

function AddUser() {
  const [form] = Form.useForm();
  const navigate = useNavigate();

  const onFinish = async (values) => {
    const payload = {
      fullname: values.fullName,
      phoneNumber: values.phone,
      email: values.email,
      password: values.password,
      retype_password: values.confirmPassword,
      role_ids: [Number(values.role)],
    };

    try {
      await axios.post("/api/users", payload, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      message.success("Thêm tài khoản thành công!");
      navigate("/admin/Account-Manager");
    } catch (err) {
      console.error("Lỗi khi thêm tài khoản:", err);
      message.error("Thêm tài khoản thất bại!");
    }
  };

  const onCancel = () => {
    form.resetFields();
    navigate("/admin/Account-Manager");
  };

  return (
    <div
      style={{
        maxWidth: 800,
        margin: "20px auto",
        padding: "24px",
        background: "#fff",
        borderRadius: "10px",
        boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
      }}
    >
      <h2 style={{ textAlign: "center", marginBottom: 24 }}>Thêm người dùng mới</h2>
      <Form layout="vertical" form={form} onFinish={onFinish}>
        <div
          style={{
            display: "grid",
            gridTemplateColumns: "1fr 1fr",
            gap: "16px",
          }}
        >
          <Form.Item
            label="Họ và tên"
            name="fullName"
            rules={[{ required: true, message: "Vui lòng nhập họ tên" }]}
          >
            <Input placeholder="Nhập họ tên" />
          </Form.Item>

          <Form.Item
            label="Số điện thoại"
            name="phone"
            rules={[
              { required: true, message: "Vui lòng nhập số điện thoại" },
              {
                pattern: /^[0-9]{9,11}$/,
                message: "Số điện thoại không hợp lệ",
              },
            ]}
          >
            <Input placeholder="Nhập số điện thoại" />
          </Form.Item>

          <Form.Item
            label="Email"
            name="email"
            rules={[
              { required: true, message: "Vui lòng nhập email" },
              { type: "email", message: "Email không hợp lệ" },
            ]}
          >
            <Input placeholder="Nhập email" />
          </Form.Item>

          <Form.Item
            label="Vai trò"
            name="role"
            rules={[{ required: true, message: "Vui lòng chọn vai trò" }]}
          >
            <Select placeholder="Chọn vai trò">
              <Option value="1">Admin</Option>
              <Option value="2">Khách hàng</Option>
              <Option value="3">Nhân viên</Option>
            </Select>
          </Form.Item>

          <Form.Item
            label="Mật khẩu"
            name="password"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu" }]}
          >
            <Input.Password placeholder="Nhập mật khẩu" />
          </Form.Item>

          <Form.Item
            label="Nhập lại mật khẩu"
            name="confirmPassword"
            dependencies={["password"]}
            rules={[
              { required: true, message: "Vui lòng nhập lại mật khẩu" },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue("password") === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(
                    new Error("Mật khẩu nhập lại không khớp!")
                  );
                },
              }),
            ]}
          >
            <Input.Password placeholder="Nhập lại mật khẩu" />
          </Form.Item>
        </div>

        <Form.Item style={{ textAlign: "center", marginTop: 16 }}>
          <Button
            type="primary"
            htmlType="submit"
            style={{
              background: "green",
              borderColor: "green",
              marginRight: 8,
            }}
          >
            Thêm
          </Button>
          <Button danger onClick={onCancel}>
            Hủy
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}

export default AddUser;
