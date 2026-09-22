# Database

## Overview

The application uses **PostgreSQL** as its relational database.

The database stores users, conversations, conversation members and messages.

The current data model is composed of four main tables:

* `users`
* `conversations`
* `conversation_members`
* `messages`

The relationships between these tables allow users to participate in conversations and exchange messages.

---

## Database Configuration

The PostgreSQL database is running in a Docker container.

### Database information

| Property           | Value                 |
| ------------------ | --------------------- |
| Database           | `cloud_chat`          |
| Username           | `chat_user`           |
| PostgreSQL version | 16                    |
| Container          | `cloud-chat-postgres` |
| Host port          | `5000`                |
| PostgreSQL port    | `5432`                |

The application connects to PostgreSQL through:

```text
jdbc:postgresql://localhost:5000/cloud_chat
```

The database password is not stored in the repository.

---

# Data Model

## Users

The `users` table stores the accounts of users registered in the application.

| Column          | Type      | Constraints      | Description                   |
| --------------- | --------- | ---------------- | ----------------------------- |
| `id`            | BIGINT    | Primary key      | Unique user identifier        |
| `username`      | VARCHAR   | NOT NULL, UNIQUE | User login/name               |
| `email`         | VARCHAR   | NOT NULL, UNIQUE | User email                    |
| `password_hash` | VARCHAR   | NOT NULL         | Hashed password               |
| `display_name`  | VARCHAR   |                  | Name displayed to other users |
| `avatar_url`    | VARCHAR   |                  | URL of the user's avatar      |
| `created_at`    | TIMESTAMP | NOT NULL         | Account creation date         |
| `updated_at`    | TIMESTAMP | NOT NULL         | Last update date              |

### Relationships

A user can:

* participate in multiple conversations;
* send multiple messages.

---

## Conversations

The `conversations` table represents a chat conversation.

| Column       | Type      | Constraints | Description                    |
| ------------ | --------- | ----------- | ------------------------------ |
| `id`         | BIGINT    | Primary key | Unique conversation identifier |
| `type`       | VARCHAR   | NOT NULL    | Type of conversation           |
| `created_at` | TIMESTAMP | NOT NULL    | Conversation creation date     |
| `updated_at` | TIMESTAMP | NOT NULL    | Last update date               |

A conversation can contain multiple users and multiple messages.

---

## Conversation Members

The `conversation_members` table associates users with conversations.

It represents the many-to-many relationship between `users` and `conversations`.

| Column            | Type      | Constraints  | Description                  |
| ----------------- | --------- | ------------ | ---------------------------- |
| `id`              | BIGINT    | Primary key  | Unique membership identifier |
| `conversation_id` | BIGINT    | NOT NULL, FK | Conversation identifier      |
| `user_id`         | BIGINT    | NOT NULL, FK | User identifier              |
| `joined_at`       | TIMESTAMP | NOT NULL     | Date when the user joined    |

### Constraints

The combination:

```text
conversation_id + user_id
```

must be unique.

This prevents the same user from being added to the same conversation more than once.

### Foreign keys

```text
conversation_id → conversations.id
user_id         → users.id
```

---

## Messages

The `messages` table stores messages sent inside conversations.

| Column            | Type      | Constraints  | Description                         |
| ----------------- | --------- | ------------ | ----------------------------------- |
| `id`              | BIGINT    | Primary key  | Unique message identifier           |
| `conversation_id` | BIGINT    | NOT NULL, FK | Conversation containing the message |
| `sender_id`       | BIGINT    | NOT NULL, FK | User who sent the message           |
| `content`         | TEXT      | NOT NULL     | Message content                     |
| `created_at`      | TIMESTAMP | NOT NULL     | Message creation date               |
| `updated_at`      | TIMESTAMP | NOT NULL     | Last update date                    |
| `status`          | VARCHAR   | NOT NULL     | Current message status              |

### Foreign keys

```text
conversation_id → conversations.id
sender_id       → users.id
```

### Indexes

Indexes are created to improve message-history queries.

```text
idx_messages_conversation_id
idx_messages_created_at
```

These indexes are particularly useful when retrieving messages belonging to a conversation and ordering them by creation date.

---

# Relationships

The main relationships are:

```text
users
  │
  │ 1
  │
  │ N
conversation_members
  │
  │ N
  │
  │ 1
conversations
  │
  │ 1
  │
  │ N
messages
```

More precisely:

```text
users
 ├── 1:N ── conversation_members
 │
 └── 1:N ── messages

conversations
 ├── 1:N ── conversation_members
 └── 1:N ── messages
```

Therefore:

* one user can belong to many conversations;
* one conversation can contain many users;
* one user can send many messages;
* one conversation can contain many messages;
* each message has one sender;
* each message belongs to one conversation.

---

# JPA Entities

The database is mapped to Java entities using **Jakarta Persistence (JPA)**.

Current entities:

```text
entity/
├── User.java
├── Conversation.java
├── ConversationMember.java
└── Message.java
```

The corresponding repositories are:

```text
repository/
├── UserRepository.java
├── ConversationRepository.java
├── ConversationMemberRepository.java
└── MessageRepository.java
```

All repositories extend Spring Data JPA's `JpaRepository`.

---

# Message History and Pagination

Message retrieval is designed to support pagination.

The `MessageRepository` provides:

```java
Page<Message> findByConversationIdOrderByCreatedAtDesc(
        Long conversationId,
        Pageable pageable
);
```

This allows the application to retrieve messages page by page instead of loading the complete conversation history into memory.

For example:

```text
conversationId = 1
page = 0
size = 20
```

retrieves a page containing up to 20 messages.

This will be used later when implementing the conversation/message API.

---

# Database Development Strategy

During the initial development phase, Hibernate is configured to automatically update the database schema:

```properties
spring.jpa.hibernate.ddl-auto=update
```

This allows the schema to evolve automatically from the JPA entities during local development.

For production environments, a controlled database migration strategy should be used instead of relying on automatic schema updates.

---

# Local Database Access

The PostgreSQL container can be accessed with:

```bash
docker exec -it cloud-chat-postgres psql -U chat_user -d cloud_chat
```

Useful PostgreSQL commands:

```sql
\dt
```

Lists the database tables.

```sql
\d users
```

Displays the structure of the `users` table.

```sql
\d conversations
```

Displays the structure of the `conversations` table.

```sql
\d conversation_members
```

Displays the structure of the `conversation_members` table.

```sql
\d messages
```

Displays the structure of the `messages` table.

To exit PostgreSQL:

```sql
\q
```

---

# Security

Database credentials must not be committed to Git.

Sensitive configuration such as:

```text
POSTGRES_PASSWORD
JWT_SECRET
AWS credentials
```

must be provided through environment variables or another secure configuration mechanism.

The repository must not contain real production credentials or secrets.
