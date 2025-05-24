import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { Avatar, AvatarFallback } from '../ui/avatar';
import { User as UserType } from '@/validation/userProfileSchemas';
import Link from 'next/link';
import { LogOut, Settings, User } from 'lucide-react';
import { logout } from '@/lib/auth';

export function ProfileMenu({ user }: { user: UserType | null }) {
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Avatar className="cursor-pointer">
          <AvatarFallback>
            {user?.userInfo?.firstName?.charAt(0) || 'U'}
            {user?.userInfo?.lastName?.charAt(0) || ''}
          </AvatarFallback>
        </Avatar>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuLabel>Account</DropdownMenuLabel>
        {user && (
          <DropdownMenuItem>
            <Link className="navbar_link" href={`/account/${user.id}`}>
              <User />
              <p>Profile</p>
            </Link>
          </DropdownMenuItem>
        )}
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
  );
}
