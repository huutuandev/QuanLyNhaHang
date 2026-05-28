import { useState, useEffect } from "react";
import { Button, message } from "antd";
import axios from "axios";

import { useNavigate, useLocation } from "react-router-dom";
import "./AddFood.scss";

function AddFood() {
  const [foodId, setFoodId] = useState(null); // ID món ăn (nếu sửa)
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [categories, setCategories] = useState([]);
  const [categoryId, setCategoryId] = useState("");

  const navigate = useNavigate();
  const location = useLocation();

  const fetchCategories = async () => {
    try {
      const res = await axios.get("/api/categories", {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      setCategories(res.data);
      if (res.data.length > 0) setCategoryId(res.data[0].id);
    } catch (err) {
      console.error("Lỗi khi lấy categories:", err);
    }
  };

  const fetchFoodDetail = async (id) => {
  try {
    const res = await axios.get(`/api/foods/${id}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    const data = res.data;

    let catName = "";
    try {
      const catRes = await axios.get(`/api/categories/${data.categoryId}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
      });
      catName = catRes.data.name;
    } catch {
      catName = "Không rõ";
    }
    setName(data.name);
    setDescription(data.description);
    setPrice(data.price);
    setImageUrl(data.imageUrl);
    setCategoryId(data.categoryId);

  } catch (err) {
    console.error("Lỗi khi lấy chi tiết món ăn:", err);
  }
};

  useEffect(() => {
    fetchCategories();
    if (location.state?.id) {
      setFoodId(location.state.id);
      fetchFoodDetail(location.state.id);
    }
  }, []);

  // Lưu dữ liệu (thêm hoặc sửa)
  const handleSubmit = async () => {
    if (!name || !price || !categoryId) {
      message.error("Vui lòng nhập đầy đủ thông tin");
      return;
    }

    const payload = {
      id: foodId || undefined,
      name,
      description,
      price: parseFloat(price),
      imageUrl,
      categoryId
    };

    try {
      if (foodId) {
        await axios.post("/api/foods", payload, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        message.success("Cập nhật món thành công!");
      } else {
        await axios.post("/api/foods", payload, {
          headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
        });
        message.success("Thêm món thành công!");
      }
      navigate("/admin/Food-List");
    } catch (err) {
      console.error("Lỗi khi lưu món:", err);
      message.error("Lưu món thất bại!");
    }
  };

  return (
    <div className="add-food-container">
      <h3>{foodId ? "Sửa món ăn" : "Thêm món ăn mới"}</h3>
      <div className="form-grid">
        <div className="form-group">
          <label>Tên món</label>
          <input type="text" value={name} onChange={e => setName(e.target.value)} placeholder="Nhập tên món" />
        </div>
        <div className="form-group">
          <label>Danh mục</label>
          <select value={categoryId} onChange={e => setCategoryId(e.target.value)}>
            {categories.map(c => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
        </div>
        <div className="form-group">
          <label>Giá</label>
          <input type="number" value={price} onChange={e => setPrice(e.target.value)} placeholder="Nhập giá" />
        </div>
        <div className="form-group">
          <label>Ảnh</label>
          <input type="text" value={imageUrl} onChange={e => setImageUrl(e.target.value)} placeholder="Nhập URL ảnh" />
        </div>
        <div className="form-group" style={{ gridColumn: "span 2" }}>
          <label>Mô tả</label>
          <textarea value={description} onChange={e => setDescription(e.target.value)} placeholder="Nhập mô tả món" />
        </div>
      </div>

      <div className="btn-group">
        <button className="btn-add" onClick={handleSubmit}>
          {foodId ? "Cập nhật" : "Thêm"}
        </button>
        <button className="btn-cancel" onClick={() => navigate("/admin/Food-List")}>Hủy</button>
      </div>
    </div>
  );
}

export default AddFood;
