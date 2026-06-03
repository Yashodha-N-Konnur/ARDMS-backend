output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "ec2_public_ip" {
  description = "EC2 instance public IP"
  value       = module.ec2.public_ip
}

output "ec2_public_dns" {
  description = "EC2 instance public DNS"
  value       = module.ec2.public_dns
}

output "rds_endpoint" {
  description = "RDS endpoint (without port)"
  value       = module.rds.db_endpoint
  sensitive   = true
}

output "application_url" {
  description = "Application URL"
  value       = "http://${module.ec2.public_dns}:8080/api"
}

output "swagger_ui_url" {
  description = "Swagger UI URL"
  value       = "http://${module.ec2.public_dns}:8080/api/swagger-ui.html"
}
