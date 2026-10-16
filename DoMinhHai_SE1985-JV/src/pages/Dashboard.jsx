import { Container, Row, Col, Card } from "react-bootstrap";
import { mockCategories, mockNews, mockUsers } from "../shared/MockDatabase";

export default function Dashboard() {
   const topAuthors = mockUsers
      .map(user => ({
         ...user,
         newsCount: mockNews.filter(news => news.authorId === user.id).length
      }))
      .sort((first, second) => second.newsCount - first.newsCount)
      .slice(0, 5);
   const maxAuthorNews = Math.max(...topAuthors.map(user => user.newsCount), 1);
   const newsStatuses = [
      { label: "Show", value: mockNews.filter(news => news.status === 1).length, className: "dashboard-bar-fill" },
      { label: "Hide", value: mockNews.filter(news => news.status !== 1).length, className: "dashboard-bar-fill dashboard-bar-fill-muted" }
   ];

   const stats = [
      { title: "News", value: mockNews.length, color: "text-primary" },
      { title: "Categories", value: mockCategories.length, color: "text-danger" },
      { title: "Users", value: mockUsers.length, color: "text-primary" }
   ];

   return (
      <Container fluid>
         <h2 className="text-white mb-4 fw-bold">Dashboard</h2>
         <Row className="g-4">
            {stats.map((stat, idx) => (
               <Col md={4} key={idx}>
                  <Card style={{ backgroundColor: 'var(--bg-panel)', borderColor: 'var(--bs-border-color)' }} className="glow-on-hover text-center py-4">
                     <Card.Body>
                        <Card.Title className="text-secondary text-uppercase">{stat.title}</Card.Title>
                        <h1 className={`display-4 fw-bold ${stat.color}`}>
                           {stat.value}
                        </h1>
                     </Card.Body>
                  </Card>
               </Col>
            ))}
         </Row>
         <Row className="g-4 mt-1">
            <Col lg={7}>
               <Card className="dashboard-chart-panel">
                  <Card.Body>
                     <Card.Title className="fw-bold">Top 5 user theo số bài viết</Card.Title>
                     {topAuthors.map(user => (
                        <div className="dashboard-chart-row" key={user.id}>
                           <div className="dashboard-chart-label">
                              <span>{user.name}</span>
                              <strong>{user.newsCount}</strong>
                           </div>
                           <div
                              className="dashboard-bar-track"
                              role="progressbar"
                              aria-label={`${user.name}: ${user.newsCount} bài viết`}
                              aria-valuenow={user.newsCount}
                              aria-valuemin="0"
                              aria-valuemax={maxAuthorNews}
                           >
                              <div className="dashboard-bar-fill" style={{ width: `${(user.newsCount / maxAuthorNews) * 100}%` }} />
                           </div>
                        </div>
                     ))}
                  </Card.Body>
               </Card>
            </Col>
            <Col lg={5}>
               <Card className="dashboard-chart-panel">
                  <Card.Body>
                     <Card.Title className="fw-bold">Trạng thái bài viết</Card.Title>
                     {newsStatuses.map(status => (
                        <div className="dashboard-chart-row" key={status.label}>
                           <div className="dashboard-chart-label">
                              <span>{status.label}</span>
                              <strong>{status.value}</strong>
                           </div>
                           <div
                              className="dashboard-bar-track"
                              role="progressbar"
                              aria-label={`${status.label}: ${status.value} bài viết`}
                              aria-valuenow={status.value}
                              aria-valuemin="0"
                              aria-valuemax={mockNews.length}
                           >
                              <div className={status.className} style={{ width: `${mockNews.length ? (status.value / mockNews.length) * 100 : 0}%` }} />
                           </div>
                        </div>
                     ))}
                  </Card.Body>
               </Card>
            </Col>
         </Row>
      </Container>
   );
}