#!/bin/bash
set -e
yum update -y
yum install -y docker git
systemctl start docker
systemctl enable docker
usermod -aG docker ec2-user

# Install Docker Compose
curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose
chmod +x /usr/local/bin/docker-compose

# Create environment file
mkdir -p /opt/ardms
cat > /opt/ardms/.env << ENVEOF
MYSQL_DATABASE=${db_name}
MYSQL_USER=${db_username}
MYSQL_PASSWORD=${db_password}
DB_URL=jdbc:mysql://${db_endpoint}:3306/${db_name}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
JWT_SECRET=${jwt_secret}
SPRING_PROFILES_ACTIVE=prod
ENVEOF

echo "ARDMS EC2 setup completed"
