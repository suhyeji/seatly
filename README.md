🪑 Seatly (실시간 좌석 예약 및 상태 관리 시스템)
"실시간 데이터 동기화와 효율적인 상태 관리를 위한 백엔드 아키텍처 설계"

Seatly는 스터디카페와 같은 공유 공간에서 사용자가 실시간으로 좌석 상태를 확인하고 예약할 수 있도록 돕는 서비스입니다. 단순한 CRUD를 넘어, 동시성 제어와 실시간 이벤트 전파를 해결하는 데 집중했습니다.

📋 Project Overview
성격: 실시간 좌석 상태 동기화 아키텍처 설계 및 구현

인원: 백엔드 2명, 모바일 1명

🛠 Tech Stack
Language & Framework: Java, Spring Boot

Database & Storage: PostgreSQL, Redis (In-Memory 상태 관리)

Real-time: WebSocket, Redis Pub/Sub

Infra & DevOps: Docker, GitHub Actions, AWS (EC2, ECR)

1. 실시간 상태 관리 (WebSocket & Redis)
Polling vs WebSocket: 불필요한 네트워크 트래픽을 줄이고 즉시성을 확보하기 위해 WebSocket을 채택했습니다.

In-Memory 전략: 빈번한 좌석 상태 변경 작업의 지연을 최소화하기 위해 DB가 아닌 Redis에서 실시간 상태를 관리합니다.

자동 만료(TTL): Redis의 TTL 기능을 활용하여 예약 시간이 종료되면 별도의 스케줄러 없이도 좌석이 자동 해제되도록 설계했습니다.

2. 동시성 제어 및 확장성
Race Condition 방지: Redis의 원자적 연산(SETNX)을 사용하여 여러 사용자가 동시에 같은 좌석을 선점하려는 문제를 해결했습니다.

분산 환경 고려: Redis Pub/Sub 구조를 도입하여, 향후 서버 인스턴스가 늘어나더라도 모든 서버의 WebSocket 클라이언트가 동일한 상태를 전달받을 수 있도록 확장성을 고려했습니다.

🚀 CI/CD Pipeline
수동 배포의 리스크를 제거하고 일관된 환경을 유지하기 위해 자동화 프로세스를 구축했습니다.

Flow: Code Push → GitHub Actions (Build/Test) → Docker Image Build → AWS ECR Push → EC2 Deployment

🔥 Troubleshooting & Lessons Learned
1. Redis 직렬화 타입 충돌 해결
문제: StringRedisSerializer와 객체 타입 간 불일치로 데이터 입출력 시 오류 발생

해결: RedisTemplate의 제네릭 타입을 명확히 지정하고 Serializer 설정을 통일하여 데이터 정합성 확보

2. Docker 네트워크 내 서비스 인식 문제
문제: 컨테이너 배포 환경에서 백엔드 서버가 Redis 컨테이너를 찾지 못하는 현상 발생

해결: 동일한 Docker Bridge Network를 구성하고, IP가 아닌 서비스 명칭(Service Name) 기반으로 연결하도록 수정하여 환경 독립성 확보

🤝 Collaboration
API Spec First: 모바일 파트와 WebSocket 이벤트 규격을 사전에 정의하여 병렬 개발 효율 극대화

Code Quality: 모든 코드는 Pull Request와 코드 리뷰를 거쳐 메인 브랜치에 반영
