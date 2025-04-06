import React from 'react';
import Link from 'next/link';
import { UserSearch } from '@/validation/userProfileSchemas';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';

function UserTile({
  user,
  onClick,
}: {
  user: UserSearch;
  onClick: () => void;
}) {
  console.log(user);
  return (
    <Link href={`/account/${user.id}`} onClick={onClick}>
      <div className="flex p-2 rounded-lg cursor-pointer hover:bg-gray-100">
        <Avatar className="w-12 h-12 shadow-md mr-2">
          <AvatarImage src="" />
          <AvatarFallback className="text-primary-500 text-[1rem]">
            {user.firstName.charAt(0)}
          </AvatarFallback>
        </Avatar>
        <h3 className="font-semibold flex items-center justify-center">
          {user.firstName} {user.lastName}
        </h3>
      </div>
    </Link>
  );
}

export default UserTile;
