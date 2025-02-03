"use client";
import Image from "next/image";
import Link from "next/link";
import { useEffect, useState } from "react";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "../ui/dropdown-menu";
import { Button } from "../ui/button";
import { Avatar, AvatarFallback } from "../ui/avatar";
import { Bell, LogOut, Plane, User } from "lucide-react";

function getCookie(name: string): string | null {
  if (typeof document === "undefined") return null;
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop()?.split(";").shift() ?? null;
  return null;
}

function deleteCookie(name: string) {
  if (typeof document !== "undefined") {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/`;
  }
}

function handleLogout(): void {
  deleteCookie("token");
  window.location.reload();
}

function Navbar() {
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const tokenFromCookie = getCookie("token");
    setToken(tokenFromCookie);
  }, []);
  return (
    <nav className="navbar">
      <div className="navbar_content">
        <Link href="/" className="-ml-1 flex items-center gap-1.5">
          <Image src="/assets/logo.svg" alt="logo" width={44} height={44} />
          <p className="text-heading3-bold max-xs:hidden">FareShare</p>
        </Link>
        <div className="flex items-center gap-6">
          {token != null ? (
            <>
              <Link className="navbar_link" href="/trips">
                <Plane />
                <p className="max-sm:hidden">Trips</p>
              </Link>

              <Link className="navbar_link" href="/notifications">
                <Bell />
                <p className="max-sm:hidden">Notifications</p>
              </Link>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Avatar className="cursor-pointer">
                    <AvatarFallback>XX</AvatarFallback>
                  </Avatar>
                </DropdownMenuTrigger>
                <DropdownMenuContent>
                  <DropdownMenuLabel>Account</DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem>
                    <Link className="navbar_link" href="/account">
                      <User />
                      <p>Profile</p>
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={handleLogout}>
                    <Link
                      className="navbar_link"
                      href={"/"}
                      onClick={handleLogout}
                    >
                      <LogOut />
                      <p>Log out</p>
                    </Link>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </>
          ) : (
            <>
              <Link className="navbar_link" href="/sign-in">
                Sign In
              </Link>
              <Button className="bg-primary-500 hover:bg-primary-600">
                <Link href="/sign-up">Sign Up - It's Free</Link>
              </Button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
