# =====================================================
# ARDMS - Terraform Variables
# =====================================================

variable "aws_region" {
  description = "AWS region for resource deployment"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Project name prefix for all resources"
  type        = string
  default     = "ardms"
}

variable "environment" {
  description = "Deployment environment (dev, staging, prod)"
  type        = string
  validation {
    condition     = contains(["dev", "staging", "prod"], var.environment)
    error_message = "Environment must be dev, staging, or prod."
  }
}

variable "vpc_cidr" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnets" {
  description = "CIDR blocks for public subnets"
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnets" {
  description = "CIDR blocks for private subnets"
  type        = list(string)
  default     = ["10.0.11.0/24", "10.0.12.0/24"]
}

variable "availability_zones" {
  description = "Availability zones"
  type        = list(string)
  default     = ["us-east-1a", "us-east-1b"]
}

variable "allowed_cidrs" {
  description = "CIDRs allowed to access the application"
  type        = list(string)
  default     = ["0.0.0.0/0"]
}

variable "ec2_instance_type" {
  description = "EC2 instance type for the application server"
  type        = string
  default     = "t3.medium"
}

variable "ami_id" {
  description = "AMI ID for EC2 instance (Amazon Linux 2023)"
  type        = string
  default     = "ami-0c02fb55956c7d316" # Amazon Linux 2023 us-east-1
}

variable "key_name" {
  description = "EC2 key pair name for SSH access"
  type        = string
}

variable "db_instance_class" {
  description = "RDS instance class"
  type        = string
  default     = "db.t3.micro"
}

variable "db_name" {
  description = "Database name"
  type        = string
  default     = "ardms_db"
}

variable "db_username" {
  description = "Database admin username"
  type        = string
  default     = "ardms_admin"
  sensitive   = true
}

variable "db_password" {
  description = "Database admin password"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "JWT secret key (256-bit minimum)"
  type        = string
  sensitive   = true
}
