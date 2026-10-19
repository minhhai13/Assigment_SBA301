import { Navbar, Container, Nav, Button } from "react-bootstrap";
import { Link, useNavigate } from "react-router";
import logo from "../images/NMS.png" 
export default function Header() {
   const navigate = useNavigate();
   let currentUser = null;
   try {
      currentUser = JSON.parse(localStorage.getItem("user") || "null");
   } catch (e) {}

   const handleLogout = () => {
      localStorage.removeItem("token");
      localStorage.removeItem("user");
      navigate("/login");
   };

   return (
      <Navbar style={{ backgroundColor: 'var(--bg-panel)' }} className="border-bottom border-secondary shadow-sm py-2" sticky="top">
         <Container fluid className="px-4">
            <Navbar.Brand as={Link} to="/" className="d-flex align-items-center gap-2 glow-on-hover rounded-circle p-1">
               <img
                  src={logo}
                  alt="FUNews Logo"
                  width="40"
                  height="40"
                  className="rounded-circle object-fit-cover"
               />
            </Navbar.Brand>
            <span className="fw-bold text-white fs-5">FUNews Admin</span>
            
            <Navbar.Collapse className="justify-content-end">
               <Nav className="me-4">
                  <Nav.Link as={Link} to="/about" className="nav-link-glow px-3">About</Nav.Link>
                  <Nav.Link as={Link} to="/contact" className="nav-link-glow px-3">Contact</Nav.Link>
               </Nav>
               
               <Navbar.Text className="me-3 text-light">
                  Xin chào, <strong className="text-info">{currentUser?.name || currentUser?.username || "Admin"}</strong>!
               </Navbar.Text>
               
               <Button variant="outline-light" size="sm" className="glow-on-hover" onClick={handleLogout}>
                  Logout
               </Button>
            </Navbar.Collapse>
         </Container>
      </Navbar>
   );
}