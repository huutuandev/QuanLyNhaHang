import { Outlet } from "react-router-dom";
import Headers from "../Component/Header";
import Footer from "../Component/Footer";

function Layout(){
    return(
        <>
        <div className="layout">
        <header className="layout__header">
            <Headers/>
        </header>
        <main className="layout__main">
            <Outlet/>
        </main >
        <footer className="layout__footer">
            <Footer/>
        </footer>
        </div>
        </>
    )
}
export default Layout;