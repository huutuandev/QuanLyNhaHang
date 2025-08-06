
import { Route, Routes } from 'react-router-dom';
import './App.css';
import Home from './Page/Home';
import Menu from './Page/Menu';
import Service from './Page/Service';
import Orther from './Page/orther';
import Error from './Page/Error';
import Layout from './Layout';
import News from './Page/News';
import Login from './Component/Login';
import Table from './Page/Table';
// import SelectTable from './Page/Table/SelectTable';
import SelectMenu from './Page/Table/SelectMenu';
import Pay from './Page/Table/Pay';
import DetailFoods from './Page/DetailFoods';
import ReservationHistory from './Page/HistoryBooking';
import PaymentSuccess from './Page/Table/PaymentSuccess';
import LayoutAdmin from './PageAdmin/LayOutAdmin';
import GridDashboard from './PageAdmin/page/Dashboard/Grid-Dashboard';
import GridPosts from './PageAdmin/page/Post-Manage/Grid-Post';
import GridUsers from './PageAdmin/page/User-Manager/Grid-User';
import GridReservation from './PageAdmin/page/Reservation- Manage/Grid-Reservation';
import GridCustomer from './PageAdmin/page/Customer-Consulting/Grid-Customer';
import FoodCategory from './PageAdmin/page/Food-Manager/Food-Category';
import DeletedFood from './PageAdmin/page/Food-Manager/Deleted-Food';
import ListFood from './PageAdmin/page/Food-Manager/List-Food';
import AddFood from './PageAdmin/page/Food-Manager/AddFood';
import AddCatagory from './PageAdmin/page/Food-Manager/AddCatagory';

function App() {
  const isLoggedIn = !!localStorage.getItem('token');
  return (
    <>

      <Routes>
        <Route path='/' element={<Layout />}>
          <Route index element={<Home />} />
          <Route path="Menu" element={<Menu />} />
          <Route path="Service" element={<Service />} />
          <Route path="Orther" element={<Orther />} />
          <Route path="News" element={<News />} />
          <Route path="Table" element={<Table />} />
          {/* <Route path="/SelectTable" element={<SelectTable />} /> */}
          <Route path="/SelectMenu" element={<SelectMenu />} />
          <Route path="/pay" element={<Pay />} />
          <Route path='/DetailFoods' element={<DetailFoods />} />
          <Route path='/ReservationHistory' element={<ReservationHistory />} />
        </Route>

        {/* trang admin */}
        <Route path="/admin" element={<LayoutAdmin />}>
          <Route index element={<GridDashboard />} />
          <Route path="Food-Category" element={<FoodCategory />} />
          <Route path="Food-List" element={<ListFood />} />
          <Route path="Add-Food" element={<AddFood />} />
          <Route path="Add-Catagory" element={<AddCatagory/>}/>
          <Route path="Delete-Food" element={<DeletedFood />} />
          <Route path="Post-Manager" element={<GridPosts />} />
          <Route path="Account-Manager" element={<GridUsers />} />
          <Route path="Booking-Manager" element={<GridReservation />} />
          <Route path="Customer-Support" element={<GridCustomer />} />
        </Route>

        <Route path="*" element={<Error />} />
        <Route path="Login" element={<Login />} />
        <Route path='/payment-Success' element={<PaymentSuccess />} />
      </Routes>
      {/* <Routes>
        <Route path="/admin" element={<LayoutAdmin />}>
          <Route index element={<GridDashboard />} />
          {/* <Route path="foods" element={<QuanLyMonAn />} />
          <Route path="posts" element={<QuanLyBaiViet />} /> */}
      {/* </Route>
      // </Routes> */}
    </>
  );
}

export default App;
