import { Container } from "react-bootstrap";

export default function Footer() {
   return (
      <footer 
         className="py-3 border-top border-secondary text-center text-light mt-auto"
         style={{ backgroundColor: 'var(--bg-panel)' }}
      >
         <Container fluid>
            <small className="opacity-75">
               &copy; {new Date().getFullYear()} FUNews Management System. Built with ReactJS.
            </small>
         </Container>
      </footer>
   );
}