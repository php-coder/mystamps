terraform {
  # @todo #1268 Update Terraform to 0.15.x
  required_version = ">= 0.13"

  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.28.1"
    }
  }
}
