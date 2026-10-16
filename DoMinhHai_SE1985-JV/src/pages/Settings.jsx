import { Container, Row, Col, Form, Button, Card } from "react-bootstrap";

export default function Settings() {
   return (
      <Container fluid>
         <h2 className="text-white fw-bold mb-4">Cài đặt Hệ thống</h2>
         <Row className="g-4">
            <Col md={6}>
               <Card style={{ backgroundColor: 'var(--bg-panel)', borderColor: 'var(--bs-border-color)' }} className="glow-on-hover">
                  <Card.Body>
                     <Card.Title className="text-white mb-3">Thông tin cá nhân</Card.Title>
                     <Form>
                        <Form.Group className="mb-3">
                           <Form.Label className="text-secondary">Họ và tên</Form.Label>
                           <Form.Control type="text" defaultValue="Admin" className="bg-dark text-white border-secondary" />
                        </Form.Group>
                        <Form.Group className="mb-4">
                           <Form.Label className="text-secondary">Email liên hệ</Form.Label>
                           <Form.Control type="email" defaultValue="admin@funews.edu.vn" className="bg-dark text-white border-secondary" />
                        </Form.Group>
                        <Button variant="light" className="glow-on-hover fw-bold">Cập nhật Profile</Button>
                     </Form>
                  </Card.Body>
               </Card>
            </Col>
            
            <Col md={6}>
               <Card style={{ backgroundColor: 'var(--bg-panel)', borderColor: 'var(--bs-border-color)' }} className="glow-on-hover">
                  <Card.Body>
                     <Card.Title className="text-white mb-3">Giao diện</Card.Title>
                     <Form>
                        <Form.Group className="mb-3">
                           <Form.Label className="text-secondary">Chế độ hiển thị</Form.Label>
                           <Form.Select className="bg-dark text-white border-secondary">
                              <option value="dark">Chế độ tối (Dark Mode)</option>
                              <option value="light">Chế độ sáng (Light Mode)</option>
                           </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-4">
                           <Form.Check 
                              type="switch"
                              id="custom-switch"
                              label="Bật hiệu ứng Glow (Hào quang)"
                              defaultChecked
                              className="text-light"
                           />
                        </Form.Group>
                        <Button variant="outline-light" className="glow-on-hover">Lưu cài đặt</Button>
                     </Form>
                  </Card.Body>
               </Card>
            </Col>
         </Row>
      </Container>
   );
}