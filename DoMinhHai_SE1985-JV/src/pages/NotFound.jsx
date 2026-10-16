import { Container, Button } from "react-bootstrap";
import { Link } from "react-router";

export default function NotFound() {
   return (
      <Container className="d-flex flex-column justify-content-center align-items-center h-100 text-center">
         <h1 className="display-1 fw-bold text-danger">404</h1>
         <h3 className="text-white mb-4">Không tìm thấy trang</h3>
         <p className="text-secondary mb-4">
            Đường dẫn bạn truy cập không tồn tại hoặc bạn không có quyền xem trang này.
         </p>
         <Button as={Link} to="/" variant="light" className="glow-on-hover fw-bold px-4 py-2">
            Quay về Dashboard
         </Button>
      </Container>
   );
}