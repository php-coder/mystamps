terraform {
  required_version = ">= 1.11"

  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.49.2"
    }
    uptimerobot = {
      source  = "vexxhost/uptimerobot"
      version = "0.8.2"
    }
    mailgun = {
      source  = "wgebis/mailgun"
      version = "0.7.7"
    }
  }
}
