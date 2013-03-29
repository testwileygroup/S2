SET DATABASE SQL SYNTAX ORA TRUE
/

-- Create tables section -------------------------------------------------

-- Table ContentStream

CREATE TABLE ContentStream (
  flid Varchar2(16 ) NOT NULL,
  stream Blob
)
/

-- Add keys for table ContentStream

ALTER TABLE ContentStream ADD CONSTRAINT ContentStreamPK PRIMARY KEY (flid)
/

-- Table FileInfo

CREATE TABLE FileInfo(
  id Varchar2(16 ) NOT NULL,
  parentFid Varchar2(16 ),
  filename Varchar2(256 ),
  extension Varchar2(10 ),
  size Integer,
  deleted Char(1 )
)
/

-- Add keys for table FileInfo

ALTER TABLE FileInfo ADD CONSTRAINT FileInfoPK PRIMARY KEY (id)
/

-- Table FolderInfo

CREATE TABLE FolderInfo(
  id Varchar2(16 ) NOT NULL,
  bucket Varchar2(20 ),
  path Varchar2(4000 ),
  deleted Char(1 )
)
/

-- Add keys for table FolderInfo

ALTER TABLE FolderInfo ADD CONSTRAINT FolderInfoPK PRIMARY KEY (id)
/




