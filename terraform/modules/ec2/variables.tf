variable "project_name"          { type = string }
variable "environment"           { type = string }
variable "instance_type"         { type = string }
variable "ami_id"                { type = string }
variable "key_name"              { type = string }
variable "public_subnet_id"      { type = string }
variable "app_security_group_id" { type = string }
variable "db_endpoint"           { type = string; sensitive = true }
variable "db_name"               { type = string }
variable "db_username"           { type = string; sensitive = true }
variable "db_password"           { type = string; sensitive = true }
variable "jwt_secret"            { type = string; sensitive = true }
