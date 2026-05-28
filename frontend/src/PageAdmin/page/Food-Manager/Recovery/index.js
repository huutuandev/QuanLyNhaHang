import { Table, Button, Image, message, Popconfirm } from 'antd';
import { useEffect, useState } from 'react';
import axios from 'axios';
// import './List-Food.scss';
import { useNavigate } from "react-router-dom";

function Recovery() {
  const navigate = useNavigate();
  const [foods, setFoods] = useState([]);
  const [allFoods, setAllFoods] = useState([]);
  const [searchName, setSearchName] = useState('');
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 5,
    total: 0,
  });

  const fetchAllFoods = async () => {
    let allData = [];
    let page = 0;
    let totalPages = 1;

    try {
      while (page < totalPages) {
        const res = await axios.get(`/api/foods/deleted?page=${page}&size=1000`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem('token')}`,
          },
        });

        const { content, totalPages: newTotalPages } = res.data;
        totalPages = newTotalPages;
        allData = [...allData, ...content];
        page++;
      }

      const foodsWithCategory = await Promise.all(
        allData.map(async (item) => {
          try {
            const catRes = await axios.get(`/api/categories/${item.categoryId}`, {
              headers: {
                Authorization: `Bearer ${localStorage.getItem('token')}`,
              },
            });
            return {
              ...item,
              categoryName: catRes.data.name,
              priceOld: item.priceOld || null,
            };
          } catch {
            return {
              ...item,
              categoryName: 'Không rõ',
              priceOld: item.priceOld || null,
            };
          }
        })
      );

      setAllFoods(foodsWithCategory);
      setFoods(foodsWithCategory);
      setPagination((prev) => ({
        ...prev,
        total: foodsWithCategory.length,
      }));
    } catch (err) {
      console.error('Lỗi khi fetch toàn bộ món ăn:', err);
    }
  };

  const handleRecovery = async (id, name) => {
    try {
      await axios.put(`/api/foods/recovery/${id}`,{}, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
        },
      });
      message.success(`Đã khôi phục món "${name}" thành công!`);
      fetchAllFoods();
    } catch (error) {
      console.error('Lỗi khi khôi phục món ăn:', error);
      message.error('khôi phục thất bại!');
    }
  };


  useEffect(() => {
    fetchAllFoods();
  }, []);

  const columns = [
    {
      title: 'STT',
      key: 'stt',
      render: (text, record, index) =>
        (pagination.current - 1) * pagination.pageSize + index + 1,
    },
    {
      title: 'Hình ảnh',
      dataIndex: 'imageUrl',
      key: 'imageUrl',
      render: (url) => <Image src={url} width={70} height={50} alt="img" />,
    },
    {
      title: 'Tên sản phẩm',
      dataIndex: 'name',
      key: 'name',
      render: (text) => <b>{text}</b>,
    },
    {
      title: 'Danh mục',
      dataIndex: 'categoryName',
      key: 'categoryName',
    },
    {
      title: 'Giá',
      key: 'price',
      render: (record) => (
        <>
          {record.priceOld && (
            <span style={{ textDecoration: 'line-through', color: 'red', marginRight: 5 }}>
              {record.priceOld.toLocaleString()} VND
            </span>
          )}
          <span>{record.price.toLocaleString()} VND</span>
        </>
      ),
    },
    {
      title: 'Thao tác',
      key: 'action',
      render: (_, record) => (
        <>
          <Popconfirm
            title="Bạn có chắc chắn muốn khôi phục món này?"
            onConfirm={() => handleRecovery(record.id, record.name)}
            okText="Khôi phục"
            cancelText="Hủy"
          >
            <Button danger>Khôi Phục</Button>
          </Popconfirm>
        </>
      ),
    },

  ];

  const handleSearch = (value) => {
    setSearchName(value);

    const filtered = allFoods.filter((item) => {
      const lowerValue = value.toLowerCase();
      const priceStr = item.price.toString();

      return (
        item.name.toLowerCase().includes(lowerValue) || 
        item.categoryName.toLowerCase().includes(lowerValue) || 
        priceStr.includes(lowerValue) 
      );
    });

    setFoods(filtered);
    setPagination((prev) => ({
      ...prev,
      current: 1,
      total: filtered.length,
    }));
  };


  return (
    <>
      <div style={{ margin: '20px' }}>
        <div className="list-food-header">
          <h3>Quản lý danh mục sản phẩm</h3>
          <div className="list-food-controls">
            <input
              type="text"
              placeholder="Tên, danh mục hoặc giá"
              className="input-control"
              value={searchName}
              onChange={(e) => handleSearch(e.target.value)}
            />

            <div className="btn-group">
              {/* <Button danger>Xóa mục đã chọn</Button>
              <Button
                type="primary"
                onClick={() => navigate("/admin/Add-Food")}
              >Thêm món</Button> */}
            </div>
          </div>
        </div>

        <Table
          columns={columns}
          dataSource={foods.slice(
            (pagination.current - 1) * pagination.pageSize,
            pagination.current * pagination.pageSize
          )}
          rowKey="id"
          pagination={{
            current: pagination.current,
            pageSize: pagination.pageSize,
            total: pagination.total,
            onChange: (page) => setPagination((prev) => ({ ...prev, current: page })),
            showSizeChanger: false,
            position: ['bottomCenter'],
          }}
        />
      </div>
    </>
  );
}

export default Recovery;
