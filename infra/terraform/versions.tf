terraform {
  required_version = ">= 0.15"

  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.28.1"
    }
    uptimerobot = {
      source  = "vexxhost/uptimerobot"
      version = "0.8.2"
    }
  }
}
