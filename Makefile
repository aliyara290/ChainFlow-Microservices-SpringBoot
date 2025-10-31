.PHONY: help build up down restart logs clean ps

# Default target
help:
	@echo "=== Docker Compose Shortcuts for Supply Chain Microservices ==="
	@echo ""
	@echo "Main Commands:"
	@echo "  make build          - Build all services"
	@echo "  make up             - Start all services"
	@echo "  make down           - Stop all services"
	@echo "  make restart        - Restart all services"
	@echo "  make logs           - View logs from all services"
	@echo "  make ps             - List running containers"
	@echo "  make clean          - Remove all containers, volumes, and images"
	@echo ""
	@echo "Individual Service Commands:"
	@echo "  make build-eureka   - Build eureka-server"
	@echo "  make build-supply   - Build supply-service"
	@echo "  make build-prod     - Build production-service"
	@echo "  make build-customer - Build customer-service"
	@echo ""
	@echo "  make up-eureka      - Start eureka-server"
	@echo "  make up-supply      - Start supply-service"
	@echo "  make up-prod        - Start production-service"
	@echo "  make up-customer    - Start customer-service"
	@echo ""
	@echo "  make restart-eureka   - Restart eureka-server"
	@echo "  make restart-supply   - Restart supply-service"
	@echo "  make restart-prod     - Restart production-service"
	@echo "  make restart-customer - Restart customer-service"
	@echo ""
	@echo "  make logs-eureka    - View eureka-server logs"
	@echo "  make logs-supply    - View supply-service logs"
	@echo "  make logs-prod      - View production-service logs"
	@echo "  make logs-customer  - View customer-service logs"
	@echo ""
	@echo "Database Commands:"
	@echo "  make logs-db        - View all database logs"
	@echo "  make pgadmin        - Open pgAdmin (http://localhost:5050)"
	@echo ""
	@echo "Development Commands:"
	@echo "  make dev            - Start all services and follow logs"
	@echo "  make rebuild        - Clean build and start everything fresh"
	@echo ""

# ============= Main Commands =============

build:
	@docker-compose build

up:
	@docker-compose up -d

down:
	@docker-compose down

restart:
	@docker-compose restart

logs:
	@docker-compose logs -f

ps:
	@docker-compose ps

clean:
	@docker-compose down -v --rmi all

build-eureka:
	@docker-compose build eureka-server

up-eureka:
	@docker-compose up -d --build eureka-server

restart-eureka:
	@docker-compose restart eureka-server

logs-eureka:
	@docker-compose logs -f eureka-server

build-supply:
	@docker-compose build supply-service

up-supply:
	@docker-compose up -d --build supply-service

restart-supply:
	@docker-compose restart supply-service

logs-supply:
	@docker-compose logs -f supply-service

build-prod:
	@docker-compose build production-service

up-prod:
	@docker-compose up -d --build production-service

restart-prod:
	@docker-compose restart production-service

logs-prod:
	@docker-compose logs -f production-service

build-customer:
	@docker-compose build customer-service

up-customer:
	@docker-compose up -d --build customer-service

restart-customer:
	@docker-compose restart customer-service

logs-customer:
	@docker-compose logs -f customer-service

logs-db:
	@docker-compose logs -f supply-db production-db customer-db

pgadmin:
	@open http://localhost:5050 || xdg-open http://localhost:5050

dev:
	@docker-compose up --build

rebuild:
	@docker-compose down
	@docker-compose down -v --rmi all
	@docker-compose build
	@docker-compose up -d


status:
	@docker-compose ps