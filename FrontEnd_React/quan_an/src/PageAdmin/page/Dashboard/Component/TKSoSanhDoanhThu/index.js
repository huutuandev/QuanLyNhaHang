import { useEffect, useState } from 'react';
import { Card } from 'antd';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import axios from 'axios';

function TKSoSanhDoanhThu() {
  const [dataChart, setDataChart] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await axios.get('/api/dashboard', {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`
          }
        });

       
        const fullMonthData = Array.from({ length: 12 }, (_, index) => ({
          name: `Tháng ${index + 1}`,
          revenue: 0
        }));

        res.data.monthlyRevenue.forEach(item => {
          const monthIndex = item.month - 1;
          fullMonthData[monthIndex].revenue = item.total;
        });

        setDataChart(fullMonthData);
      } catch (error) {
        console.error("Lỗi khi lấy dữ liệu doanh thu theo tháng:", error);
      }
    };

    fetchData();
  }, []);

  return (
    <Card title="So Sánh Doanh Thu">
      <ResponsiveContainer width="100%" height={400}>
        <LineChart data={dataChart}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis />
          <Tooltip />
          <Legend />
          <Line type="monotone" dataKey="revenue" stroke="#1890ff" />
        </LineChart>
      </ResponsiveContainer>
    </Card>
  );
}

export default TKSoSanhDoanhThu;
