# =====================================================
# ARDMS - Terraform Root Module
# Provisions AWS infrastructure for ARDMS application
# =====================================================

terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  # Remote state backend (configure for your S3 bucket)
  backend "s3" {
    bucket         = "ardms-terraform-state"
    key            = "ardms/terraform.tfstate"
    region         = "us-east-1"
    encrypt        = true
    dynamodb_table = "ardms-terraform-locks"
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "ARDMS"
      Environment = var.environment
      ManagedBy   = "Terraform"
      Owner       = "DevOps"
    }
  }
}

# ── VPC Module ─────────────────────────────────────
module "vpc" {
  source = "./modules/vpc"

  project_name       = var.project_name
  environment        = var.environment
  vpc_cidr           = var.vpc_cidr
  public_subnets     = var.public_subnets
  private_subnets    = var.private_subnets
  availability_zones = var.availability_zones
}

# ── Security Groups Module ─────────────────────────
module "security_groups" {
  source = "./modules/security_groups"

  project_name = var.project_name
  environment  = var.environment
  vpc_id       = module.vpc.vpc_id
  vpc_cidr     = var.vpc_cidr
  allowed_cidrs = var.allowed_cidrs
}

# ── RDS MySQL Module ───────────────────────────────
module "rds" {
  source = "./modules/rds"

  project_name          = var.project_name
  environment           = var.environment
  db_name               = var.db_name
  db_username           = var.db_username
  db_password           = var.db_password
  db_instance_class     = var.db_instance_class
  private_subnet_ids    = module.vpc.private_subnet_ids
  db_security_group_id  = module.security_groups.db_security_group_id
}

# ── EC2 Application Server Module ─────────────────
module "ec2" {
  source = "./modules/ec2"

  project_name          = var.project_name
  environment           = var.environment
  instance_type         = var.ec2_instance_type
  ami_id                = var.ami_id
  key_name              = var.key_name
  public_subnet_id      = module.vpc.public_subnet_ids[0]
  app_security_group_id = module.security_groups.app_security_group_id
  db_endpoint           = module.rds.db_endpoint
  db_name               = var.db_name
  db_username           = var.db_username
  db_password           = var.db_password
  jwt_secret            = var.jwt_secret
}
