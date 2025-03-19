'use client';
import React from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { useEffect, useState } from 'react';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { Button } from '../ui/button';
import { Avatar, AvatarFallback } from '../ui/avatar';
import { Bell, LogOut, Plane, Settings, User } from 'lucide-react';
import { decodeToken, getToken, isLoggedIn, logout } from '@/lib/auth';

function Navbar() {
  const [token, setToken] = useState<string | null>(null);
  const [id, setId] = useState<string | null>(null);

  useEffect(() => {
    const token = getToken();
    setToken(token);

    if (token) {
      const decode = decodeToken(token);
      setId(decode?.sub ?? '');
    }
  }, []);
  return (
    <nav className="navbar">
      <div className="navbar_content">
        <Link
          href={token && isLoggedIn(token) ? '/trips' : '/'}
          className="-ml-1 flex items-center gap-1.5"
        >
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
                  <DropdownMenuItem>
                    <Link className="navbar_link" href={`/account/${id}`}>
                      <User />
                      <p>Profile</p>
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem>
                    <Link className="navbar_link" href="/account">
                      <Settings />
                      <p>Settings</p>
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem>
                    <Link className="navbar_link" href="/" onClick={logout}>
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
                <Link href="/sign-up">Sign Up - It&apos;s Free</Link>
              </Button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
