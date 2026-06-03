resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-${var.environment}-db-subnet-group"
  subnet_ids = var.private_subnet_ids
  tags       = { Name = "${var.project_name}-${var.environment}-db-subnet-group" }
}

resource "aws_db_instance" "mysql" {
  identifier             = "${var.project_name}-${var.environment}-mysql"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = var.db_instance_class
  allocated_storage      = 20
  max_allocated_storage  = 100
  storage_type           = "gp2"
  storage_encrypted      = true
  db_name                = var.db_name
  username               = var.db_username
  password               = var.db_password
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [var.db_security_group_id]
  publicly_accessible    = false
  skip_final_snapshot    = false
  final_snapshot_identifier = "${var.project_name}-${var.environment}-final-snapshot"
  backup_retention_period = 7
  backup_window           = "03:00-04:00"
  maintenance_window      = "Mon:04:00-Mon:05:00"
  deletion_protection     = var.environment == "prod" ? true : false
  multi_az               = var.environment == "prod" ? true : false

  tags = { Name = "${var.project_name}-${var.environment}-mysql" }
}
