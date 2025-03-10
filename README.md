# FareShare

FareShare is a collaborative travel planning application designed to help you and your friends organize trips seamlessly. With features like group creation, friendships management, notifications, voting on attractions, and more, FareShare makes trip planning fun and efficient.

---

## Table of Contents

- [About the Project](#about-the-project)
  - [Features](#features)
- [Built With](#built-with)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Usage](#usage)
- [Contributors](#contributors)
- [Acknowledgements](#acknowledgements)
- [License](#license)

---

## About the Project

FareShare is a full-stack application built to simplify group travel planning. Whether you're organizing a weekend getaway or a long vacation, FareShare provides tools to manage your trip collaboratively. From voting on attractions to splitting bills (coming soon) and sharing photos (coming soon), FareShare ensures everyone stays connected and involved.

---

### Features

- **Group Creation**: Create and manage travel groups with your friends.
- **Friendships Management**: Add and manage friends within the app.
- **Notifications**: Stay updated with real-time notifications.
- **Voting on Attractions**: Vote on attractions to include in your trip itinerary.
- **Bill Splitting**: (Coming Soon) Easily split expenses among group members.
- **Shared Photo Bucket**: (Coming Soon) Share and store trip memories in a shared photo bucket.

---

## Built With

This section lists the major frameworks, libraries, and tools used to build FareShare.

### Backend

### Backend

- [![Java][Java]][blank]
- [![Spring Boot][Spring Boot]][blank]
- [![Postgres][Postgres]][blank]
- [![Docker][Docker]][blank]
- [![JUnit][JUnit]][blank]
- [![Mockito][Mockito]][blank]
- [![Jacoco][Jacoco]][blank]

### Frontend

- [![Next.js][Next.js]][blank]
- [![TypeScript][TypeScript]][blank]
- [![Shadcn][Shadcn]][blank]
- [![TailwindCSS][TailwindCSS]][blank]

[Java]: https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Spring Boot]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white
[Postgres]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white
[Docker]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[JUnit]: https://img.shields.io/badge/JUnit-25A162?style=for-the-badge&logo=junit5&logoColor=white
[Mockito]: https://img.shields.io/badge/Mockito-78C257?style=for-the-badge&logo=mockito&logoColor=white
[Jacoco]: https://img.shields.io/badge/JaCoCo-78C257?style=for-the-badge&logo=jacoco&logoColor=white
[Next.js]: https://img.shields.io/badge/Next.js-000000?style=for-the-badge&logo=nextdotjs&logoColor=white
[TypeScript]: https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white
[Shadcn]: https://img.shields.io/badge/shadcn%2Fui-000?logo=shadcnui&logoColor=fff
[TailwindCSS]: https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white
[blank]: https://github.com/your-username/your-repo

---

## Getting Started

Follow these steps to set up FareShare locally on your machine.

### Prerequisites

Before you begin, ensure you have the following installed:

- Java 21
- Node.js (for frontend)
- Docker
- PostgreSQL

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-username/FareShare.git
   cd FareShare
   ```

2. **Set up the backend**

- In the main directory run:
  ```bash
  docker-compose up
  ```

3. **Set up the frontend**

- In the `frontend` directory run:
  ```bash
  npm install
  npm run dev
  ```

4. **Access the application**

- Backend: `localhost:8080`
- Frontend: `localhost:3000`

## Usage

Once the application is running, you can:

- Create an account and log in.

- Create or join a travel group.

- Add friends and manage your connections.

- Vote on attractions and plan your itinerary.

- Stay updated with real-time notifications.

## Contributors

FareShare was developed by:

<a href="https://github.com/TravelApplication/fare-share/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=TravelApplication/fare-share" />
</a>

<small>
- Agata Bartczak
- Oskar Gawryszewski
- Szymon Grabski
- Sebastian Jabłoński
</small>

## Acknowledgements

We would like to thank the following resources and tools that helped us build FareShare:

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

- [Next.js Documentation](https://nextjs.org/docs)

- [TailwindCSS Documentation](https://tailwindcss.com/docs/installation/using-vite)

- [Shadcn UI](https://ui.shadcn.com/)
