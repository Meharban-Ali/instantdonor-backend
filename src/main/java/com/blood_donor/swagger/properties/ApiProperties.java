/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.boot.context.properties.ConfigurationProperties
 *  org.springframework.stereotype.Component
 */
package com.blood_donor.swagger.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="app.api")
public class ApiProperties {
    private String title = "AI Blood Donor Network API";
    private String version = "1.0.0";
    private String description = "Emergency Blood Donation Platform";
    private Contact contact = new Contact();
    private License license = new License();
    private Server server = new Server();

    public String getTitle() {
        return this.title;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return this.description;
    }

    public Contact getContact() {
        return this.contact;
    }

    public License getLicense() {
        return this.license;
    }

    public Server getServer() {
        return this.server;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public static class Contact {
        private String name = "Blood Donor Network Team";
        private String email = "support@blooddonor.com";
        private String url = "https://blooddonor.com";

        public String getName() {
            return this.name;
        }

        public String getEmail() {
            return this.email;
        }

        public String getUrl() {
            return this.url;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class License {
        private String name = "MIT License";
        private String url = "https://opensource.org/licenses/MIT";

        public String getName() {
            return this.name;
        }

        public String getUrl() {
            return this.url;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class Server {
        private String devUrl = "http://localhost:8080";
        private String devDescription = "Development Server";
        private String prodUrl = "https://api.blooddonor.com";
        private String prodDescription = "Production Server";

        public String getDevUrl() {
            return this.devUrl;
        }

        public String getDevDescription() {
            return this.devDescription;
        }

        public String getProdUrl() {
            return this.prodUrl;
        }

        public String getProdDescription() {
            return this.prodDescription;
        }

        public void setDevUrl(String devUrl) {
            this.devUrl = devUrl;
        }

        public void setDevDescription(String devDescription) {
            this.devDescription = devDescription;
        }

        public void setProdUrl(String prodUrl) {
            this.prodUrl = prodUrl;
        }

        public void setProdDescription(String prodDescription) {
            this.prodDescription = prodDescription;
        }
    }
}

