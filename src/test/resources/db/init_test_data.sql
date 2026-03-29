INSERT INTO users (id, user_role, email, username, avatar_key, password) VALUES
(1, 'USER', 'user1@mail.com', 'user1', null, '$2a$12$vaE2vIW5oWwcb4uhWtPyZOWwN/m5WB6Vt.P4LfBaj.s5llmmddlXS'),
(2, 'USER', 'user2@mail.com', 'user2', null, '$2a$12$vaE2vIW5oWwcb4uhWtPyZOWwN/m5WB6Vt.P4LfBaj.s5llmmddlXS'),
(3, 'USER', 'user3@mail.com', 'user3', null, '$2a$12$vaE2vIW5oWwcb4uhWtPyZOWwN/m5WB6Vt.P4LfBaj.s5llmmddlXS'),
(4, 'ADMIN', 'admin@mail.com', 'admin', null, '$2a$12$vaE2vIW5oWwcb4uhWtPyZOWwN/m5WB6Vt.P4LfBaj.s5llmmddlXS'),
(5, 'SUPER_ADMIN', 'superamin@mail.com', 'super admin', null, '$2a$12$vaE2vIW5oWwcb4uhWtPyZOWwN/m5WB6Vt.P4LfBaj.s5llmmddlXS');

INSERT INTO artist (id, user_id, artist_name, description) VALUES
(1, 1, 'Artist One', 'First artist'),
(2, 2, 'Artist Two', 'Second artist');

INSERT INTO track (id, title, file_key, main_artist_id, duration_ms, release_date) VALUES
(1, 'Track One', 'track1.mp3', 1, 180000, '2026-03-29'),
(2, 'Track Two', 'track2.mp3', 1, 200000, '2026-03-29'),
(3, 'Track Three', 'track3.mp3', 2, 210000, '2026-03-29');

INSERT INTO artists_feat_tracks (id, artist_id, track_id) VALUES
(1, 2, 1), -- Artist 2 feat в Track 1
(2, 1, 3); -- Artist 1 feat в Track 3

INSERT INTO favorites (id, user_id, track_id) VALUES
(1, 1, 3), -- User 1 like Track 3
(2, 1, 1), -- User 1 like Track 1
(3, 2, 1), -- User 2 like Track 1
(4, 3, 2); -- User 2 like Track 2

INSERT INTO subscriptions (id, artist_id, subscriber_id) VALUES
(1, 1, 2), -- User 2 подписан на Artist 1
(2, 2, 1); -- User 1 подписан на Artist 2

-- fix sequences
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('artist_id_seq', (SELECT MAX(id) FROM artist));
SELECT setval('track_id_seq', (SELECT MAX(id) FROM track));
SELECT setval('artists_feat_tracks_id_seq', (SELECT MAX(id) FROM artists_feat_tracks));
SELECT setval('favorites_id_seq', (SELECT MAX(id) FROM favorites));
SELECT setval('subscriptions_id_seq', (SELECT MAX(id) FROM subscriptions));