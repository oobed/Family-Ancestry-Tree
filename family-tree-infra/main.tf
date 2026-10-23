terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

# Configure the AWS Provider
provider "aws" {
  region = "us-west-2"
}

#create vpc
resource "aws_vpc" "main"{
  cidr_block = "10.0.0.0/16"
  enable_dns_support = true
  enable_dns_hostnames = true

  tags = {
    Name = "family-tree-vpc"
  }
}

resource "aws_subnet" "public"{
    vpc_id = aws_vpc.main.id
    cidr_block = "10.0.1.0/24"
    availability_zone = "us-west-2a"
    map_public_ip_on_launch = true

    tags = {
        Name = "family-tree-public-subnet"
    }
}

resource "aws_subnet" "private"{
    vpc_id = aws_vpc.main.id
    cidr_block = "10.0.2.0/24"
    availability_zone = "us-west-2a"

    tags = {
        Name = "family-tree-private-subnet"
    }
}

resource "aws_subnet" "private-b"{
    vpc_id = aws_vpc.main.id
    cidr_block = "10.0.3.0/24"
    availability_zone = "us-west-2b"

    tags = {
        Name = "family-tree-private-subnet-b"
    }
}

resource "aws_db_subnet_group" "default" {
  name       = "main"
  subnet_ids = [aws_subnet.private.id, aws_subnet.private-b.id]

  tags = {
    Name = "family-tree-db-subnet-group"
  }
}

variable "db_password" {
    description = "The password for the RDS instance"
    type = string
    sensitive = true
}

resource "aws_db_instance" "main" {
  allocated_storage           = 20
  identifier                  = "family-tree-db-vpc"
  db_name                     = "nigerian_village_family_tree"
  engine                      = "mysql"
  instance_class              = "db.t4g.micro"
  username                    = "oobed"
  db_subnet_group_name        = aws_db_subnet_group.default.name
  vpc_security_group_ids      = [aws_security_group.rds_sg.id]
  password                    = var.db_password
  skip_final_snapshot          = true
  publicly_accessible         = false

  tags = {
    Name = "family-tree-db"
  }
}

output "rds_endpoint" {
  value = aws_db_instance.main.address
}



resource "aws_internet_gateway" "main"{
    vpc_id = aws_vpc.main.id
    
    tags = {
        Name = "family-tree-igw"
    }
}

resource "aws_route_table" "public"{
    vpc_id = aws_vpc.main.id

    route {
        cidr_block = "0.0.0.0/0"
        gateway_id = aws_internet_gateway.main.id
    }

    tags = {
        Name = "family-tree-public-rt"
    }
}

resource "aws_route_table_association" "public"{
    subnet_id = aws_subnet.public.id
    route_table_id = aws_route_table.public.id
}

#security group for EC2 instance
resource "aws_security_group" "ec2_sg" {
  name        = "family-tree-ec2-sg"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "family-tree-ec2-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "ec2_sg_ssh" {
  security_group_id = aws_security_group.ec2_sg.id
  description      = "Allow SSH from IP"
  cidr_ipv4         = "${var.my_IP}/32"
  from_port         = 22
  ip_protocol       = "tcp"
  to_port           = 22
}

resource "aws_vpc_security_group_ingress_rule" "ec2_sg_traffic" {
  security_group_id = aws_security_group.ec2_sg.id
  description      = "App traffic"
  cidr_ipv4         = "0.0.0.0/0"
  from_port         = 8080
  ip_protocol       = "tcp"
  to_port           = 8080
}

resource "aws_vpc_security_group_egress_rule" "ec2_sg_egress" {
  security_group_id = aws_security_group.ec2_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1" # semantically equivalent to all ports
}


#security group for RDS instance
resource "aws_security_group" "rds_sg" {
  name        = "family-tree-rds-sg"
  vpc_id      = aws_vpc.main.id

  tags = {
    Name = "family-tree-rds-sg"
  }
}

resource "aws_vpc_security_group_ingress_rule" "rds_sg_ingress" {
  security_group_id = aws_security_group.rds_sg.id
  description      = "Allow RDS from IP"
  from_port         = 3306
  ip_protocol       = "tcp"
  to_port           = 3306
  referenced_security_group_id = aws_security_group.ec2_sg.id
}



resource "aws_vpc_security_group_egress_rule" "rds_sg_egress" {
  security_group_id = aws_security_group.rds_sg.id
  cidr_ipv4         = "0.0.0.0/0"
  ip_protocol       = "-1" # semantically equivalent to all ports
}

variable "my_IP" {
    description = "My public IP address for ssh access"
    type = string
}

#ami instance
resource "aws_instance" "app" {
    ami = "ami-0d53cc9bd365ad65b"
    instance_type = "t2.micro"
    subnet_id = aws_subnet.public.id
    vpc_security_group_ids = [aws_security_group.ec2_sg.id]
    key_name = "FT"

    tags = {
        Name = "family-tree-app"
    }
}



output "ec2_public_ip" {
  value = aws_instance.app.public_ip
}



#use this command to apply the terraform configuration utilizing the my IP variable
# terraform apply -var="my_IP=$((Invoke-RestMethod https://checkip.amazonaws.com).Trim())"  (PowerShell; variable name is case-sensitive)