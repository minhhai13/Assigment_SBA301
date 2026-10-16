import { Nav } from "react-bootstrap";
import { NavLink } from "react-router";

export default function Sidebar() {
   return (
      <div 
         className="p-3 d-flex flex-column border-end border-secondary shadow" 
         style={{ width: "250px", height: "100%", backgroundColor: 'var(--bg-panel)', overflowY: "auto" }}
      >
         <div className="text-uppercase text-secondary fw-bold mb-3 mt-2 px-2" style={{ fontSize: "12px", letterSpacing: "1px" }}>
            Quản lý hệ thống
         </div>
         <Nav className="flex-column gap-2">
            <Nav.Link as={NavLink} to="/" end className="nav-link-glow p-2">
               Dashboard
            </Nav.Link>
            <Nav.Link as={NavLink} to="/category" className="nav-link-glow p-2">
               Category
            </Nav.Link>
            <Nav.Link as={NavLink} to="/news" className="nav-link-glow p-2">
               News
            </Nav.Link>
            <Nav.Link as={NavLink} to="/users" className="nav-link-glow p-2">
               Users
            </Nav.Link>
            
            <hr className="border-secondary my-2 opacity-50" />
            
            <Nav.Link as={NavLink} to="/settings" className="nav-link-glow p-2">
               Settings
            </Nav.Link>
         </Nav>
      </div>
   );
}