
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
import SelectTable from './Page/Table/SelectTable';
import SelectMenu from './Page/Table/SelectMenu';
import Pay from './Page/Table/Pay';
import DetailFoods from './Page/DetailFoods';
import HistoryBooking from './Page/HistoryBooking';
import PaymentSuccess from './Page/Table/PaymentSuccess';
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
          <Route path='/HistoryBooking' element={<HistoryBooking />} />
        </Route>
        <Route path="*" element={<Error />} />
        <Route path="Login" element={<Login />} />
        <Route path='/payment-Success' element={<PaymentSuccess />} />
      </Routes>
    </>
  );
}

export default App;
