import { useState, useEffect } from "react";
import { Button, message } from "antd";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom";
import "./AddCatagory.scss";

function AddCategory() {
  const [name, setName] = useState("");
  const navigate = useNavigate();
  const location = useLocation();
  const id = location.state?.id; // Lấy id từ state khi sửa

  useEffect(() => {
    if (id) {
      axios.get(`/api/categories/${id}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      })
        .then(res => setName(res.data.name || ""))
        .catch(err => {
          console.error("Lỗi khi lấy danh mục:", err);
          message.error("Không lấy được thông tin danh mục!");
        });
    }
  }, [id]);

  const handleSubmit = async () => {
    if (!name.trim()) {
      message.error("Vui lòng nhập tên danh mục");
      return;
    }

    try {
      await axios.post("/api/categories",
        id ? { id: parseInt(id), name } : { name },
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      message.success(id ? "Cập nhật danh mục thành công!" : "Thêm danh mục thành công!");
      navigate("/admin/Food-Category");
    } catch (err) {
      console.error("Lỗi khi lưu danh mục:", err);
      message.error("Lưu danh mục thất bại!");
    }
  };

  return (
    <div className="add-food-container">
      <h3>{id ? "Cập nhật danh mục" : "Thêm danh mục mới"}</h3>
      <div className="form-grid">
        <div className="form-group">
          <label>Tên danh mục</label>
          <input
            type="text"
            value={name}
            onChange={e => setName(e.target.value)}
            placeholder="Nhập tên danh mục"
          />
        </div>
      </div>

      <div className="btn-group">
        <button className="btn-add" onClick={handleSubmit}>
          {id ? "Cập nhật" : "Thêm"}
        </button>
        <button className="btn-cancel" onClick={() => navigate("/admin/Food-Category")}>
          Hủy
        </button>
      </div>
    </div>
  );
}

export default AddCategory;
