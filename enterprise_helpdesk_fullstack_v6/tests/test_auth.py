def test_root(client):
    response = client.get("/")
    assert response.status_code == 200
    assert response.json()["message"] == "Enterprise Helpdesk API online"


def test_login(client):
    response = client.post(
        "/api/v1/auth/login",
        data={"username": "admin@empresa.com", "password": "Admin1234"},
    )
    assert response.status_code == 200
    data = response.json()
    assert "access_token" in data
    assert data["token_type"] == "bearer"
