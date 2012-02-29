package org.example.client.config;

public class DataBaseConnection {
	@Override
	public String toString() {
		return "DataBaseConnection [username=" + username + ", password="
				+ password + ", connection_url=" + connection_url + "]";
	}

	public String username;
	public String password;
	public String connection_url;

	public DataBaseConnection(String username, String password,
			String connection_url) {
		this.username = username;
		this.password = password;
		this.connection_url = connection_url;
	}
}