import { useState } from "react";
import { Container, Form, Button, Card, Alert } from "react-bootstrap";
import { useNavigate } from "react-router";

export default function Login() {
   const [username, setUsername] = useState("");
   const [password, setPassword] = useState("");
   const [error, setError] = useState("");
   const navigate = useNavigate();

   const handleLogin = (e) => {
      e.preventDefault();
      if (username === "Admin" && password === "Admin") {
         navigate("/");
      } else {
         setError("Tài khoản hoặc mật khẩu không chính xác!");
      }
   };

   return (
      <div className="d-flex align-items-center justify-content-center vh-100 vw-100" style={{ backgroundColor: 'var(--bg-dark-blue)' }}>
         <Card style={{ width: "400px", backgroundColor: 'var(--bg-panel)', borderColor: 'var(--bs-border-color)' }} className="shadow-lg p-3 glow-on-hover">
            <Card.Body>
               <div className="text-center mb-4">
                  <h2 className="text-white fw-bold">FUNews Login</h2>
               </div>
               
               {error && <Alert variant="danger">{error}</Alert>}
               
               <Form onSubmit={handleLogin}>
                  <Form.Group className="mb-3">
                     <Form.Label className="text-light">Username</Form.Label>
                     <Form.Control 
                        type="text" 
                        placeholder="Enter Admin" 
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="bg-dark text-white border-secondary"
                     />
                  </Form.Group>

                  <Form.Group className="mb-4">
                     <Form.Label className="text-light">Password</Form.Label>
                     <Form.Control 
                        type="password" 
                        placeholder="Enter Admin" 
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="bg-dark text-white border-secondary"
                     />
                  </Form.Group>

                  <Button variant="light" type="submit" className="w-100 fw-bold glow-on-hover">
                     Login
                  </Button>
               </Form>
            </Card.Body>
         </Card>
      </div>
   );
}