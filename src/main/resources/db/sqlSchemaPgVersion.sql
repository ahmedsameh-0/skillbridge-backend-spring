-- ========================
-- ENUM TYPES
-- ========================

CREATE TYPE user_role AS ENUM ('user', 'admin');
CREATE TYPE user_status AS ENUM ('active', 'blocked');

CREATE TYPE skill_type AS ENUM ('offer', 'want');
CREATE TYPE skill_level AS ENUM ('beginner', 'intermediate', 'advanced');

CREATE TYPE match_status AS ENUM ('pending', 'accepted', 'rejected');

CREATE TYPE session_status AS ENUM ('pending', 'active', 'completed', 'canceled');

CREATE TYPE conversation_type AS ENUM ('match', 'session', 'direct');

-- ========================
-- USERS
-- ========================

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(150) UNIQUE,
    password_hash VARCHAR(255),

    role user_role DEFAULT 'user',
    status user_status DEFAULT 'active',

    email_verified_at TIMESTAMP NULL,
    last_login_at TIMESTAMP NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================
-- PROFILES (1-1)
-- ========================

CREATE TABLE profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE,

    bio TEXT,
    avatar_url VARCHAR(255),
    country VARCHAR(100),

    rating_avg DECIMAL(3,2) DEFAULT 0.00,
    total_reviews INT DEFAULT 0,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_profile_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ========================
-- CATEGORIES (SELF RELATION)
-- ========================

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    parent_id BIGINT NULL,

    name VARCHAR(100) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_category_parent
        FOREIGN KEY (parent_id)
        REFERENCES categories(id)
        ON DELETE SET NULL
);

-- ========================
-- SKILLS
-- ========================

CREATE TABLE skills (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL,

    name VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_skill_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
        ON DELETE CASCADE
);

-- ========================
-- USER_SKILLS (M-M)
-- ========================

CREATE TABLE user_skills (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL,
    skill_id BIGINT NOT NULL,

    type skill_type,
    level skill_level,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_us_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_us_skill
        FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE,

    UNIQUE (user_id, skill_id, type)
);

-- ========================
-- MATCHES
-- ========================

CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,

    user1_id BIGINT,
    user2_id BIGINT,

    user1_skill_id BIGINT,
    user2_skill_id BIGINT,

    status match_status DEFAULT 'pending',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user1_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (user2_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (user1_skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE,

    FOREIGN KEY (user2_skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

-- ========================
-- SESSIONS
-- ========================

CREATE TABLE sessions (
    id BIGSERIAL PRIMARY KEY,

    match_id BIGINT,

    host_user_id BIGINT,
    guest_user_id BIGINT,

    skill_id BIGINT,

    status session_status DEFAULT 'pending',

    scheduled_at TIMESTAMP NULL,
    started_at TIMESTAMP NULL,
    ended_at TIMESTAMP NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (match_id)
        REFERENCES matches(id)
        ON DELETE CASCADE,

    FOREIGN KEY (host_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (guest_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (skill_id)
        REFERENCES skills(id)
        ON DELETE CASCADE
);

-- ========================
-- CONVERSATIONS
-- ========================

CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,

    type conversation_type,

    match_id BIGINT NULL,
    session_id BIGINT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (match_id)
        REFERENCES matches(id)
        ON DELETE CASCADE,

    FOREIGN KEY (session_id)
        REFERENCES sessions(id)
        ON DELETE CASCADE
);

-- ========================
-- MESSAGES
-- ========================

CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,

    conversation_id BIGINT,
    sender_id BIGINT,

    message_text TEXT,

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (conversation_id)
        REFERENCES conversations(id)
        ON DELETE CASCADE,

    FOREIGN KEY (sender_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ========================
-- REVIEWS
-- ========================

CREATE TABLE reviews (
    id BIGSERIAL PRIMARY KEY,

    reviewer_id BIGINT,
    reviewed_user_id BIGINT,

    rating INT CHECK (rating BETWEEN 1 AND 5),

    comment TEXT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (reviewer_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    FOREIGN KEY (reviewed_user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ========================
-- NOTIFICATIONS
-- ========================

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT,

    title VARCHAR(150),
    body TEXT,

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ========================
-- ACTIVITY LOGS
-- ========================

CREATE TABLE activity_logs (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT,

    action VARCHAR(150),
    description TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

-- ========================
-- UPDATED_AT AUTO UPDATE
-- ========================

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
   NEW.updated_at = CURRENT_TIMESTAMP;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- TRIGGERS

CREATE TRIGGER trg_users_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_profiles_updated_at
BEFORE UPDATE ON profiles
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_categories_updated_at
BEFORE UPDATE ON categories
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_skills_updated_at
BEFORE UPDATE ON skills
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();


CREATE TRIGGER trg_user_skills_updated_at
BEFORE UPDATE ON user_skills
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_matches_updated_at
BEFORE UPDATE ON matches
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_sessions_updated_at
BEFORE UPDATE ON sessions
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_conversations_updated_at
BEFORE UPDATE ON conversations
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_reviews_updated_at
BEFORE UPDATE ON reviews
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER trg_notifications_updated_at
BEFORE UPDATE ON notifications
FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();