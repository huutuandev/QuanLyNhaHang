import { Table, Button, Image, Popconfirm, message, Input } from "antd";
import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { SearchOutlined } from "@ant-design/icons";

function GridPost() {
  const navigate = useNavigate();
  const [posts, setPosts] = useState([]);
  const [searchValue, setSearchValue] = useState("");

  useEffect(() => {
    fetchPosts();
  }, []);

  const fetchPosts = () => {
    axios.get("/api/posts")
      .then((res) => {
        setPosts(res.data.content);
      })
      .catch((err) => {
        console.error(err);
        message.error("Không thể tải danh sách bài viết.");
      });
  };

  const handleDelete = async (id) => {
    console.log("ID cần xoá:", id);
    try {
      const res = await axios.delete(`/api/posts/${id}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` }
      }
      );
      console.log("Phản hồi xoá:", res);
      message.success("Xoá bài viết thành công!");
      setPosts((prev) => prev.filter((post) => post.id !== id));
    } catch (err) {
      console.error("Lỗi xoá:", err?.response || err);
      message.error("Xoá bài viết thất bại!");
    }
  };


  const filteredPosts = posts.filter((post) =>
    post.title.toLowerCase().includes(searchValue.toLowerCase()) ||
    post.authorName.toLowerCase().includes(searchValue.toLowerCase())
  );

  const columns = [
    {
      title: "Tiêu đề",
      dataIndex: "title",
      key: "title",
    },
    {
      title: "Nội dung",
      dataIndex: "content",
      key: "content",
    },
    {
      title: "Tác giả",
      dataIndex: "authorName",
      key: "authorName",
    },
    {
      title: "Ảnh",
      dataIndex: "imageUrl",
      key: "imageUrl",
      render: (text) => <Image width={100} src={text} />,
    },
    {
      title: "Hành động",
      key: "action",
      render: (_, record) => (
        <>
          <Button onClick={() => navigate(`/admin/edit-post`,{ state: { id: record.id } })}>
            Sửa
          </Button>
          <Popconfirm
            title="Bạn có chắc muốn xoá bài viết này?"
            onConfirm={() => handleDelete(record.id)}
            okText="Xóa"
            cancelText="Hủy"
            placement="topLeft"
          >
            <Button danger style={{ marginLeft: 8 }}>Xoá</Button>
          </Popconfirm>
        </>
      )
    },
  ];

  return (
    <div>
      <div style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        marginBottom: 16
      }}>
        <h2 style={{ margin: 0 }}>Danh sách</h2>
        <Input
          placeholder="Tìm kiếm bài viết hoặc tác giả ở đây!"
          prefix={<SearchOutlined />}
          value={searchValue}
          onChange={(e) => setSearchValue(e.target.value)}
          style={{ width: 300, borderRadius: 8 }}
        />
      </div>

      <div style={{ marginBottom: 16, display: "flex", justifyContent: "flex-end" }}>
        <Button type="primary" onClick={() => navigate("/admin/edit-post")}>Thêm bài viết</Button>
      </div>

      <Table columns={columns} dataSource={filteredPosts} rowKey="id"
        pagination={{
          position: ["bottomCenter"]
        }} />
    </div>
  );
}

export default GridPost;
