ALTER TABLE tracks
    ADD COLUMN IF NOT EXISTS audio_url VARCHAR(1000);

UPDATE tracks
SET audio_url = 'https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/7c/4c/3e/7c4c3e1e-0d63-5b3e-87b4-fdc7e9b2b87d/mzaf_16843829759888499836.plus.aac.p.m4a'
WHERE title = 'Save Your Tears';

UPDATE tracks
SET audio_url = 'https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/36/3b/47/363b47a6-7d6f-1c7a-0c74-3b7e1b2a1a84/mzaf_15988925539584737331.plus.aac.p.m4a'
WHERE title = 'Blinding Lights';

UPDATE tracks
SET audio_url = 'https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/77/75/0d/77750d6b-2a1c-0a6b-5a6c-5b3c3c7c8d6a/mzaf_17621353284758837924.plus.aac.p.m4a'
WHERE title = 'Hardest To Love';

UPDATE tracks
SET audio_url = 'https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/0c/0b/7a/0c0b7a2e-5d8e-0b6f-3e7a-6d4f3c5b6e7d/mzaf_13122345784599234223.plus.aac.p.m4a'
WHERE title = 'Too Late';

UPDATE tracks
SET audio_url = 'https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview125/v4/9f/7c/1d/9f7c1d6b-3a7d-2b1f-8e5c-6a7c8d9e1f2a/mzaf_16784593284512345678.plus.aac.p.m4a'
WHERE title = 'Alone Again';