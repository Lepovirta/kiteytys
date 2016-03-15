CREATE TABLE owner (
    id VARCHAR PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE game (
    -- Basic info
    id                SERIAL PRIMARY KEY,
    owner             VARCHAR NOT NULL REFERENCES owner (id),
    email             VARCHAR NOT NULL,
    createdAt         TIMESTAMP NOT NULL DEFAULT current_timestamp,

    -- Strong
    strongCard        VARCHAR NOT NULL,
    strongNum         INT NOT NULL,

    -- Weak
    weakCard          VARCHAR NOT NULL,
    weakNum           INT NOT NULL,

    -- Important
    importantCard     VARCHAR NOT NULL,
    importantNum      INT NOT NULL,

    -- Hard
    hardCard          VARCHAR NOT NULL,
    hardNum           INT NOT NULL,

    -- Tedious
    tediousCard       VARCHAR NOT NULL,
    tediousNum        INT NOT NULL,

    -- Inspiring
    inspiringCard     VARCHAR NOT NULL,
    inspiringNum      INT NOT NULL,

    -- Misc
    topaasia          VARCHAR NOT NULL,
    topaasiaAnswer    VARCHAR NOT NULL,
    rating            INT NOT NULL
);