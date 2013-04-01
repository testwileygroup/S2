package com.wiley.cms.vela.service.commons.dctm;


import java.util.Date;

/**
 * @author <a href="eantaev@wiley.com">Evgeny Antaev</a>
 * @since 2/24/11
 */
public class DctmDocument {
    private String objectId;
    private String objectName;
    private String objectType;
    private String creator;
    private String description;
    private String lockOwner;
    private String lockMachine;
    private Date lockDate;
    private String modifier;
    private String contentType;
    private String contentSize;
    private String folderId;


    public DctmDocument() {
    }

    public String getContentSize() {
        return contentSize;
    }

    public void setContentSize(String contentSize) {
        this.contentSize = contentSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getLockDate() {
        return lockDate;
    }

    public void setLockDate(Date lockDate) {
        this.lockDate = lockDate;
    }

    public String getLockMachine() {
        return lockMachine;
    }

    public void setLockMachine(String lockMachine) {
        this.lockMachine = lockMachine;
    }

    public String getLockOwner() {
        return lockOwner;
    }

    public void setLockOwner(String lockOwner) {
        this.lockOwner = lockOwner;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
}